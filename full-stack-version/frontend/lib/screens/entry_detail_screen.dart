import 'package:flutter/material.dart';
import '../models/diary_entry.dart';
import '../services/api_service.dart';
import 'add_entry_screen.dart';

class EntryDetailScreen extends StatefulWidget {
  final DiaryEntry entry;
  final int userId;
  const EntryDetailScreen({super.key, required this.entry, required this.userId});
  @override
  State<EntryDetailScreen> createState() => _EntryDetailScreenState();
}

class _EntryDetailScreenState extends State<EntryDetailScreen> {
  late DiaryEntry _entry;

  static const Color pc = Color(0xFF6C5CE7);
  static const Color bg = Color(0xFFF5F6FA);
  static const Color textDark = Color(0xFF2D3436);
  static const Color textGray = Color(0xFF636E72);
  static const Color border = Color(0xFFDFE6E9);

  final _moodColors = <String, Color>{
    'Happy': Color(0xFFF9CA24), 'Sad': Color(0xFF4A90E2),
    'Excited': Color(0xFFFF9F43), 'Anxious': Color(0xFFE74C3C),
    'Calm': Color(0xFF00B894), 'Angry': Color(0xFFD63031),
    'Grateful': Color(0xFF6C5CE7),
  };
  final _moodIcons = <String, IconData>{
    'Happy': Icons.sentiment_very_satisfied,
    'Sad': Icons.sentiment_very_dissatisfied,
    'Excited': Icons.star,
    'Anxious': Icons.warning_amber_outlined,
    'Calm': Icons.spa_outlined,
    'Angry': Icons.whatshot_outlined,
    'Grateful': Icons.favorite_outline,
  };

  @override
  void initState() {
    super.initState();
    _entry = widget.entry;
  }

  Future<void> _delete() async {
    final ok = await showDialog<bool>(context: context, builder: (_) => AlertDialog(
      title: const Text('Delete Entry', style: TextStyle(color: textDark, fontWeight: FontWeight.bold)),
      content: Text('Delete "${_entry.title}"?\nThis cannot be undone.', style: const TextStyle(color: textGray)),
      actions: [
        TextButton(onPressed: () => Navigator.pop(context, false),
          child: const Text('Cancel', style: TextStyle(color: textGray))),
        ElevatedButton(onPressed: () => Navigator.pop(context, true),
          style: ElevatedButton.styleFrom(backgroundColor: Colors.red, elevation: 0,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))),
          child: const Text('Delete', style: TextStyle(color: Colors.white))),
      ]));
    if (ok == true) {
      await ApiService.deleteEntry(_entry.id, widget.userId);
      if (mounted) Navigator.pop(context, 'deleted');
    }
  }

  Future<void> _edit() async {
    final result = await Navigator.push(context,
      MaterialPageRoute(builder: (_) => AddEntryScreen(userId: widget.userId, entry: _entry)));
    if (result == true && mounted) {
      // Refresh entries and pop back
      Navigator.pop(context, 'updated');
    }
  }

  @override
  Widget build(BuildContext context) {
    final mc = _moodColors[_entry.mood] ?? pc;
    final moodIcon = _moodIcons[_entry.mood] ?? Icons.edit_note;
    final dateStr = _entry.entryDate.isNotEmpty
      ? _entry.entryDate.substring(0, 10)
      : (_entry.createdAt.length >= 10 ? _entry.createdAt.substring(0, 10) : _entry.createdAt);

    return Scaffold(backgroundColor: bg,
      appBar: AppBar(backgroundColor: Colors.white, elevation: 0,
        surfaceTintColor: Colors.transparent,
        bottom: PreferredSize(preferredSize: const Size.fromHeight(1), child: Divider(height: 1, color: border)),
        leading: IconButton(icon: const Icon(Icons.arrow_back, color: textDark), onPressed: () => Navigator.pop(context)),
        title: const Text('View Entry', style: TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 17)),
        actions: [
          IconButton(icon: const Icon(Icons.edit_outlined, color: pc), onPressed: _edit, tooltip: 'Edit'),
          IconButton(icon: Icon(Icons.delete_outline, color: Colors.red.shade400), onPressed: _delete, tooltip: 'Delete'),
          const SizedBox(width: 4),
        ]),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [

          // Top color bar card
          Container(width: double.infinity,
            decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(16),
              border: Border.all(color: border),
              boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.04), blurRadius: 10, offset: const Offset(0, 3))]),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              // Colored top bar
              Container(height: 5, decoration: BoxDecoration(color: mc, borderRadius: const BorderRadius.vertical(top: Radius.circular(16)))),
              Padding(padding: const EdgeInsets.all(20), child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [

                // Title + Favorite
                Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Expanded(child: Text(_entry.title,
                    style: const TextStyle(color: textDark, fontSize: 22, fontWeight: FontWeight.bold, height: 1.3))),
                  if (_entry.isFavorite)
                    Container(padding: const EdgeInsets.all(6),
                      decoration: BoxDecoration(color: Colors.red.shade50, shape: BoxShape.circle),
                      child: Icon(Icons.favorite, color: Colors.red.shade400, size: 18)),
                ]),
                const SizedBox(height: 14),

                // Meta info row
                Wrap(spacing: 10, runSpacing: 8, children: [
                  // Date
                  _metaChip(Icons.calendar_today_outlined, dateStr, textGray, const Color(0xFFF0F0F5)),
                  // Mood
                  _metaChip(moodIcon, _entry.mood, mc, mc.withOpacity(0.1)),
                  // Category
                  _metaChip(Icons.folder_outlined, _entry.category, textGray, const Color(0xFFF0F0F5)),
                ]),
              ])),
            ])),

          const SizedBox(height: 16),

          // Content section
          Container(width: double.infinity, padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(16),
              border: Border.all(color: border),
              boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.04), blurRadius: 10, offset: const Offset(0, 3))]),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Row(children: [
                Container(padding: const EdgeInsets.all(6),
                  decoration: BoxDecoration(color: mc.withOpacity(0.1), borderRadius: BorderRadius.circular(8)),
                  child: Icon(Icons.notes, color: mc, size: 16)),
                const SizedBox(width: 8),
                const Text('Entry', style: TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 14)),
              ]),
              const Divider(height: 20),
              Text(_entry.content,
                style: const TextStyle(color: textDark, fontSize: 15, height: 1.8,
                  letterSpacing: 0.2, fontWeight: FontWeight.w400)),
            ])),

          const SizedBox(height: 16),

          // Action buttons
          Row(children: [
            Expanded(child: OutlinedButton.icon(
              onPressed: _edit,
              icon: const Icon(Icons.edit_outlined, size: 16),
              label: const Text('Edit Entry'),
              style: OutlinedButton.styleFrom(foregroundColor: pc,
                side: const BorderSide(color: pc),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                padding: const EdgeInsets.symmetric(vertical: 12)))),
            const SizedBox(width: 12),
            Expanded(child: OutlinedButton.icon(
              onPressed: _delete,
              icon: const Icon(Icons.delete_outline, size: 16),
              label: const Text('Delete'),
              style: OutlinedButton.styleFrom(foregroundColor: Colors.red.shade500,
                side: BorderSide(color: Colors.red.shade300),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                padding: const EdgeInsets.symmetric(vertical: 12)))),
          ]),
          const SizedBox(height: 30),
        ])));
  }

  Widget _metaChip(IconData icon, String label, Color textColor, Color bgColor) {
    return Container(padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(color: bgColor, borderRadius: BorderRadius.circular(8)),
      child: Row(mainAxisSize: MainAxisSize.min, children: [
        Icon(icon, color: textColor, size: 13),
        const SizedBox(width: 5),
        Text(label, style: TextStyle(color: textColor, fontSize: 12, fontWeight: FontWeight.w600)),
      ]));
  }
}
