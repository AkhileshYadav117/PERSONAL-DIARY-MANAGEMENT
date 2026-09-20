import 'package:flutter/material.dart';
import '../models/diary_entry.dart';
import '../services/api_service.dart';

class AddEntryScreen extends StatefulWidget {
  final int userId;
  final DiaryEntry? entry;
  const AddEntryScreen({super.key, required this.userId, this.entry});
  @override
  State<AddEntryScreen> createState() => _AddEntryScreenState();
}

class _AddEntryScreenState extends State<AddEntryScreen> {
  final _titleCtrl = TextEditingController();
  final _contentCtrl = TextEditingController();
  String _mood = 'Happy';
  String _cat = 'Personal';
  bool _fav = false;
  bool _loading = false;
  String _error = '';
  bool get _isEdit => widget.entry != null;

  static const Color pc = Color(0xFF6C5CE7);
  static const Color bg = Color(0xFFF5F6FA);
  static const Color textDark = Color(0xFF2D3436);
  static const Color textGray = Color(0xFF636E72);
  static const Color border = Color(0xFFDFE6E9);

  final _moods = ['Happy','Sad','Excited','Anxious','Calm','Angry','Grateful'];
  final _cats = ['Personal','Work','Travel','Health','General','Family','Study'];
  final _moodColors = <String,Color>{'Happy':Color(0xFFF9CA24),'Sad':Color(0xFF4A90E2),'Excited':Color(0xFFFF9F43),'Anxious':Color(0xFFE74C3C),'Calm':Color(0xFF00B894),'Angry':Color(0xFFD63031),'Grateful':Color(0xFF6C5CE7)};
  final _moodIcons = <String,IconData>{'Happy':Icons.sentiment_very_satisfied,'Sad':Icons.sentiment_very_dissatisfied,'Excited':Icons.star,'Anxious':Icons.warning_amber_outlined,'Calm':Icons.spa_outlined,'Angry':Icons.whatshot_outlined,'Grateful':Icons.favorite_outline};

  @override
  void initState() {
    super.initState();
    if (_isEdit) { _titleCtrl.text = widget.entry!.title; _contentCtrl.text = widget.entry!.content; _mood = widget.entry!.mood; _cat = widget.entry!.category; _fav = widget.entry!.isFavorite; }
  }

  Future<void> _save() async {
    if (_titleCtrl.text.trim().isEmpty) { setState(() => _error = 'Title is required'); return; }
    if (_contentCtrl.text.trim().isEmpty) { setState(() => _error = 'Content is required'); return; }
    setState(() { _loading = true; _error = ''; });
    final e = DiaryEntry(id: _isEdit ? widget.entry!.id : 0, userId: widget.userId,
      title: _titleCtrl.text.trim(), content: _contentCtrl.text.trim(),
      mood: _mood, category: _cat, isFavorite: _fav, entryDate: '', createdAt: '');
    final r = _isEdit ? await ApiService.updateEntry(widget.entry!.id, e) : await ApiService.addEntry(e);
    setState(() => _loading = false);
    if (r['success'] == true) { if (mounted) Navigator.pop(context, true); }
    else { setState(() => _error = r['message'] ?? 'Failed to save'); }
  }

  @override
  Widget build(BuildContext context) {
    final mc = _moodColors[_mood] ?? pc;
    return Scaffold(backgroundColor: bg,
      appBar: AppBar(backgroundColor: Colors.white, elevation: 0, surfaceTintColor: Colors.transparent,
        bottom: PreferredSize(preferredSize: const Size.fromHeight(1), child: Divider(height: 1, color: border)),
        leading: IconButton(icon: const Icon(Icons.arrow_back, color: textDark), onPressed: () => Navigator.pop(context)),
        title: Text(_isEdit ? 'Edit Entry' : 'New Diary Entry', style: const TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 17)),
        actions: [
          IconButton(icon: Icon(_fav ? Icons.favorite : Icons.favorite_border, color: _fav ? Colors.red : textGray),
            onPressed: () => setState(() => _fav = !_fav), tooltip: 'Favourite'),
          Padding(padding: const EdgeInsets.only(right: 12),
            child: ElevatedButton(onPressed: _loading ? null : _save,
              style: ElevatedButton.styleFrom(backgroundColor: pc, foregroundColor: Colors.white, elevation: 0,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10)),
              child: _loading ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                  : Text(_isEdit ? 'Update' : 'Save', style: const TextStyle(fontWeight: FontWeight.w600)))),
        ]),
      body: SingleChildScrollView(padding: const EdgeInsets.all(16), child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        // Mood
        Container(padding: const EdgeInsets.all(16), decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(12), border: Border.all(color: border)),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            const Text('How are you feeling?', style: TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 14)),
            const SizedBox(height: 12),
            SizedBox(height: 72, child: ListView(scrollDirection: Axis.horizontal, children: _moods.map((m) {
              final sel = _mood == m; final c = _moodColors[m]!;
              return GestureDetector(onTap: () => setState(() => _mood = m),
                child: Container(margin: const EdgeInsets.only(right: 10), width: 70,
                  decoration: BoxDecoration(color: sel ? c.withOpacity(0.1) : bg, borderRadius: BorderRadius.circular(10),
                    border: Border.all(color: sel ? c : border, width: sel ? 1.5 : 1)),
                  child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                    Icon(_moodIcons[m]!, color: sel ? c : textGray, size: 24),
                    const SizedBox(height: 4),
                    Text(m, style: TextStyle(color: sel ? c : textGray, fontSize: 10, fontWeight: sel ? FontWeight.bold : FontWeight.normal)),
                  ])));}).toList())),
          ])),
        const SizedBox(height: 12),
        // Category
        Container(padding: const EdgeInsets.all(16), decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(12), border: Border.all(color: border)),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            const Text('Category', style: TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 14)),
            const SizedBox(height: 10),
            Wrap(spacing: 8, runSpacing: 8, children: _cats.map((c) { final sel = _cat == c;
              return GestureDetector(onTap: () => setState(() => _cat = c),
                child: Container(padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
                  decoration: BoxDecoration(color: sel ? pc : bg, borderRadius: BorderRadius.circular(20),
                    border: Border.all(color: sel ? pc : border)),
                  child: Text(c, style: TextStyle(color: sel ? Colors.white : textGray, fontSize: 12, fontWeight: sel ? FontWeight.w600 : FontWeight.normal))));}).toList()),
          ])),
        const SizedBox(height: 12),
        // Title + Content
        Container(padding: const EdgeInsets.all(16), decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(12), border: Border.all(color: border)),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            const Text('Title', style: TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 14)),
            const SizedBox(height: 8),
            TextField(controller: _titleCtrl, style: const TextStyle(color: textDark, fontSize: 16, fontWeight: FontWeight.w600),
              decoration: InputDecoration(hintText: 'Entry title...', hintStyle: const TextStyle(color: textGray),
                filled: true, fillColor: bg,
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: const BorderSide(color: border)),
                enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: const BorderSide(color: border)),
                focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: BorderSide(color: mc, width: 1.5)),
                contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12))),
            const SizedBox(height: 16),
            const Text('Content', style: TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 14)),
            const SizedBox(height: 8),
            TextField(controller: _contentCtrl, maxLines: 12, style: const TextStyle(color: textDark, fontSize: 14, height: 1.6),
              decoration: InputDecoration(hintText: "What's on your mind today?", hintStyle: const TextStyle(color: textGray),
                filled: true, fillColor: bg, contentPadding: const EdgeInsets.all(14),
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: const BorderSide(color: border)),
                enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: const BorderSide(color: border)),
                focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: BorderSide(color: mc, width: 1.5)))),
            if (_error.isNotEmpty) ...[const SizedBox(height: 10),
              Container(padding: const EdgeInsets.all(10), decoration: BoxDecoration(color: Colors.red.shade50, borderRadius: BorderRadius.circular(8), border: Border.all(color: Colors.red.shade200)),
                child: Row(children: [Icon(Icons.error_outline, color: Colors.red.shade400, size: 16), const SizedBox(width: 8),
                  Expanded(child: Text(_error, style: TextStyle(color: Colors.red.shade700, fontSize: 13)))]))],
          ])),
        const SizedBox(height: 80),
      ])));
  }
}