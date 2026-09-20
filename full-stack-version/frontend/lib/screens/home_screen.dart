import 'package:flutter/material.dart';
import '../models/diary_entry.dart';
import '../services/api_service.dart';
import 'login_screen.dart';
import 'add_entry_screen.dart';
import 'file_screen.dart';
import 'calendar_screen.dart';
import 'entry_detail_screen.dart';

class HomeScreen extends StatefulWidget {
  final int userId;
  final String userName;
  const HomeScreen({super.key, required this.userId, required this.userName});
  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  List<DiaryEntry> _entries = [];
  List<DiaryEntry> _filtered = [];
  bool _loading = true;
  String _search = '';
  String _mood = 'All';
  final _searchCtrl = TextEditingController();
  static const Color pc = Color(0xFF6C5CE7);
  static const Color bg = Color(0xFFF5F6FA);
  static const Color textDark = Color(0xFF2D3436);
  static const Color textGray = Color(0xFF636E72);
  static const Color border = Color(0xFFDFE6E9);
  final _moods = ['All','Happy','Sad','Excited','Anxious','Calm','Angry','Grateful'];
  final _moodColors = <String,Color>{'Happy':Color(0xFFF9CA24),'Sad':Color(0xFF4A90E2),'Excited':Color(0xFFFF9F43),'Anxious':Color(0xFFE74C3C),'Calm':Color(0xFF00B894),'Angry':Color(0xFFD63031),'Grateful':Color(0xFF6C5CE7)};

  @override void initState() { super.initState(); _load(); }

  Future<void> _load() async {
    setState(() => _loading = true);
    final e = await ApiService.getEntries(widget.userId);
    setState(() { _entries = e; _loading = false; });
    _filter();
  }

  void _filter() {
    setState(() { _filtered = _entries.where((e) =>
      (_mood == 'All' || e.mood == _mood) &&
      (_search.isEmpty || e.title.toLowerCase().contains(_search.toLowerCase()) || e.content.toLowerCase().contains(_search.toLowerCase()))
    ).toList(); });
  }

  Future<void> _delete(DiaryEntry e) async {
    final ok = await showDialog<bool>(context: context, builder: (_) => AlertDialog(
      title: const Text('Delete Entry', style: TextStyle(color: textDark, fontWeight: FontWeight.bold)),
      content: Text('Delete "${e.title}"?', style: const TextStyle(color: textGray)),
      actions: [
        TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancel', style: TextStyle(color: textGray))),
        ElevatedButton(onPressed: () => Navigator.pop(context, true),
          style: ElevatedButton.styleFrom(backgroundColor: Colors.red, elevation: 0, shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))),
          child: const Text('Delete', style: TextStyle(color: Colors.white))),
      ]));
    if (ok == true) { await ApiService.deleteEntry(e.id, widget.userId); _load(); }
  }

  String _fmt(String raw) {
    if (raw.isEmpty) return '';
    try { return raw.length >= 10 ? raw.substring(0, 10) : raw; } catch (_) { return raw; }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(backgroundColor: bg,
      appBar: AppBar(backgroundColor: Colors.white, elevation: 0, surfaceTintColor: Colors.transparent,
        bottom: PreferredSize(preferredSize: const Size.fromHeight(1), child: Divider(height: 1, color: border)),
        title: Row(children: [
          Container(padding: const EdgeInsets.all(6), decoration: BoxDecoration(color: pc.withOpacity(0.1), borderRadius: BorderRadius.circular(8)),
            child: const Icon(Icons.book, color: pc, size: 20)),
          const SizedBox(width: 10),
          Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            const Text('Personal Diary', style: TextStyle(color: textDark, fontSize: 17, fontWeight: FontWeight.bold)),
            Text('Hello, ${widget.userName}!', style: const TextStyle(color: textGray, fontSize: 11)),
          ]),
        ]),
        actions: [
          IconButton(icon: const Icon(Icons.refresh, color: textGray), onPressed: _load, tooltip: 'Refresh'),
          IconButton(
            icon: const Icon(Icons.calendar_month_outlined, color: textGray),
            tooltip: 'Calendar',
            onPressed: () async {
              await Navigator.push(context, MaterialPageRoute(builder: (_) => CalendarScreen(userId: widget.userId)));
              _load();
            }),
          IconButton(
            icon: const Icon(Icons.folder_outlined, color: textGray),
            tooltip: 'Backup & Export',
            onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => FileScreen(userId: widget.userId)))),
          IconButton(icon: const Icon(Icons.logout, color: textGray), tooltip: 'Logout',
            onPressed: () => Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const LoginScreen()))),
          const SizedBox(width: 4),
        ]),
      body: Column(children: [
        Container(color: Colors.white, padding: const EdgeInsets.fromLTRB(16,12,16,12),
          child: Row(children: [
            _stat('${_entries.length}', 'Total Entries', Icons.notes_outlined, pc),
            const SizedBox(width: 10),
            _stat('${_entries.where((e) => e.isFavorite).length}', 'Favourites', Icons.favorite_outline, Colors.red),
            const SizedBox(width: 10),
            _stat('${_entries.where((e) => e.mood == "Happy").length}', 'Happy Days', Icons.wb_sunny_outlined, const Color(0xFFF9CA24)),
          ])),
        Divider(height: 1, color: border),
        Container(color: Colors.white, padding: const EdgeInsets.fromLTRB(16,10,16,10),
          child: TextField(controller: _searchCtrl, style: const TextStyle(color: textDark, fontSize: 14),
            onChanged: (v) { _search = v; _filter(); },
            decoration: InputDecoration(hintText: 'Search diary entries...', hintStyle: const TextStyle(color: textGray),
              prefixIcon: const Icon(Icons.search, color: textGray, size: 18),
              suffixIcon: _search.isNotEmpty ? IconButton(icon: const Icon(Icons.clear, color: textGray, size: 18),
                onPressed: () { _searchCtrl.clear(); _search = ''; _filter(); }) : null,
              filled: true, fillColor: bg,
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: const BorderSide(color: border)),
              enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: const BorderSide(color: border)),
              focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: const BorderSide(color: pc)),
              contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10)))),
        Container(color: Colors.white, padding: const EdgeInsets.fromLTRB(12,0,12,10),
          child: SizedBox(height: 34, child: ListView.builder(scrollDirection: Axis.horizontal, itemCount: _moods.length,
            itemBuilder: (_, i) {
              final m = _moods[i]; final sel = _mood == m;
              return GestureDetector(onTap: () { setState(() => _mood = m); _filter(); },
                child: Container(margin: const EdgeInsets.only(right: 8),
                  padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
                  decoration: BoxDecoration(color: sel ? pc : bg, borderRadius: BorderRadius.circular(20), border: Border.all(color: sel ? pc : border)),
                  child: Text(m, style: TextStyle(color: sel ? Colors.white : textGray, fontSize: 12, fontWeight: sel ? FontWeight.w600 : FontWeight.normal))));
            }))),
        Divider(height: 1, color: border),
        Expanded(child: _loading
          ? const Center(child: CircularProgressIndicator(color: pc))
          : _filtered.isEmpty
            ? Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                Icon(Icons.book_outlined, color: Colors.grey.shade300, size: 64),
                const SizedBox(height: 12),
                Text(_search.isEmpty && _mood == 'All' ? 'No diary entries yet\nTap + to write your first entry!' : 'No entries found',
                  textAlign: TextAlign.center, style: TextStyle(color: Colors.grey.shade400, fontSize: 15))]))
            : ListView.builder(padding: const EdgeInsets.all(16), itemCount: _filtered.length, itemBuilder: (_, i) => _card(_filtered[i]))),
      ]),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () async { final r = await Navigator.push(context, MaterialPageRoute(builder: (_) => AddEntryScreen(userId: widget.userId))); if (r == true) _load(); },
        backgroundColor: pc, elevation: 2,
        icon: const Icon(Icons.add, color: Colors.white),
        label: const Text('New Entry', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w600))));
  }

  Widget _stat(String val, String label, IconData icon, Color c) {
    return Expanded(child: Container(padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
      decoration: BoxDecoration(color: c.withOpacity(0.08), borderRadius: BorderRadius.circular(10), border: Border.all(color: c.withOpacity(0.2))),
      child: Row(children: [Icon(icon, color: c, size: 18), const SizedBox(width: 8),
        Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(val, style: TextStyle(color: c, fontSize: 16, fontWeight: FontWeight.bold)),
          Text(label, style: TextStyle(color: c.withOpacity(0.8), fontSize: 10)),
        ]))])));
  }

  Widget _card(DiaryEntry e) {
    final mc = _moodColors[e.mood] ?? pc;
    final dateStr = _fmt(e.entryDate.isNotEmpty ? e.entryDate : e.createdAt);
    return GestureDetector(
      onTap: () async {
        final result = await Navigator.push(context,
          MaterialPageRoute(builder: (_) => EntryDetailScreen(entry: e, userId: widget.userId)));
        if (result == 'deleted' || result == 'updated') _load();
      },
      child: Container(margin: const EdgeInsets.only(bottom: 12),
        decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(12), border: Border.all(color: border),
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.04), blurRadius: 8, offset: const Offset(0,2))]),
        child: Column(children: [
          Container(height: 3, decoration: BoxDecoration(color: mc, borderRadius: const BorderRadius.vertical(top: Radius.circular(12)))),
          Padding(padding: const EdgeInsets.all(14), child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Container(padding: const EdgeInsets.all(8), decoration: BoxDecoration(color: mc.withOpacity(0.1), borderRadius: BorderRadius.circular(8)),
              child: Icon(Icons.edit_note, color: mc, size: 22)),
            const SizedBox(width: 12),
            Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Row(children: [
                Expanded(child: Text(e.title, style: const TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 15), maxLines: 1, overflow: TextOverflow.ellipsis)),
                if (e.isFavorite) const Icon(Icons.favorite, color: Colors.red, size: 15),
              ]),
              const SizedBox(height: 4),
              Text(e.content, style: const TextStyle(color: textGray, fontSize: 13), maxLines: 2, overflow: TextOverflow.ellipsis),
              const SizedBox(height: 8),
              Row(children: [
                Container(padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                  decoration: BoxDecoration(color: mc.withOpacity(0.1), borderRadius: BorderRadius.circular(6), border: Border.all(color: mc.withOpacity(0.3))),
                  child: Text(e.mood, style: TextStyle(color: mc, fontSize: 11, fontWeight: FontWeight.w600))),
                const SizedBox(width: 6),
                Container(padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                  decoration: BoxDecoration(color: bg, borderRadius: BorderRadius.circular(6), border: Border.all(color: border)),
                  child: Text(e.category, style: const TextStyle(color: textGray, fontSize: 11))),
                const Spacer(),
                const Icon(Icons.calendar_today_outlined, size: 11, color: textGray),
                const SizedBox(width: 4),
                Text(dateStr, style: const TextStyle(color: textGray, fontSize: 11)),
              ]),
            ])),
            Column(children: [
              IconButton(icon: const Icon(Icons.edit_outlined, color: textGray, size: 17),
                onPressed: () async { final r = await Navigator.push(context, MaterialPageRoute(builder: (_) => AddEntryScreen(userId: widget.userId, entry: e))); if (r == true) _load(); },
                padding: EdgeInsets.zero, constraints: const BoxConstraints(minWidth: 32, minHeight: 32)),
              IconButton(icon: Icon(Icons.delete_outline, color: Colors.red.shade400, size: 17),
                onPressed: () => _delete(e), padding: EdgeInsets.zero, constraints: const BoxConstraints(minWidth: 32, minHeight: 32)),
            ]),
          ])),
        ])));
  }
}