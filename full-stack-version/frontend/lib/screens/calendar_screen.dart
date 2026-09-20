import 'package:flutter/material.dart';
import 'package:table_calendar/table_calendar.dart';
import 'package:intl/intl.dart';
import '../models/diary_entry.dart';
import '../services/api_service.dart';
import 'add_entry_screen.dart';
import 'entry_detail_screen.dart';

class CalendarScreen extends StatefulWidget {
  final int userId;
  const CalendarScreen({super.key, required this.userId});
  @override
  State<CalendarScreen> createState() => _CalendarScreenState();
}

class _CalendarScreenState extends State<CalendarScreen> {
  DateTime _focusedDay = DateTime.now();
  DateTime? _selectedDay;
  List<DiaryEntry> _allEntries = [];
  List<DiaryEntry> _selectedEntries = [];
  bool _loading = true;

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

  @override
  void initState() {
    super.initState();
    _selectedDay = DateTime.now();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    final entries = await ApiService.getEntries(widget.userId);
    setState(() {
      _allEntries = entries;
      _loading = false;
    });
    _updateSelected(_selectedDay!);
  }

  // Get entries for a specific date
  List<DiaryEntry> _getEntriesForDay(DateTime day) {
    return _allEntries.where((e) {
      try {
        if (e.entryDate.isEmpty && e.createdAt.isEmpty) return false;
        final raw = e.entryDate.isNotEmpty ? e.entryDate : e.createdAt;
        final entryDate = DateTime.parse(raw.substring(0, 10));
        return isSameDay(entryDate, day);
      } catch (_) { return false; }
    }).toList();
  }

  void _updateSelected(DateTime day) {
    setState(() {
      _selectedDay = day;
      _selectedEntries = _getEntriesForDay(day);
    });
  }

  // Check if any entry exists on a day (for markers)
  bool _hasEntry(DateTime day) => _getEntriesForDay(day).isNotEmpty;

  @override
  Widget build(BuildContext context) {
    return Scaffold(backgroundColor: bg,
      appBar: AppBar(backgroundColor: Colors.white, elevation: 0,
        surfaceTintColor: Colors.transparent,
        bottom: PreferredSize(preferredSize: const Size.fromHeight(1), child: Divider(height: 1, color: border)),
        leading: IconButton(icon: const Icon(Icons.arrow_back, color: textDark), onPressed: () => Navigator.pop(context)),
        title: const Text('Calendar', style: TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 17)),
        actions: [
          IconButton(icon: const Icon(Icons.today, color: pc),
            onPressed: () { setState(() { _focusedDay = DateTime.now(); _selectedDay = DateTime.now(); }); _updateSelected(DateTime.now()); },
            tooltip: 'Today'),
          const SizedBox(width: 8),
        ]),
      body: _loading
        ? const Center(child: CircularProgressIndicator(color: pc))
        : Column(children: [
            // Calendar Widget
            Container(color: Colors.white, child: TableCalendar(
              firstDay: DateTime.utc(2020, 1, 1),
              lastDay: DateTime.utc(2030, 12, 31),
              focusedDay: _focusedDay,
              selectedDayPredicate: (day) => isSameDay(_selectedDay, day),
              onDaySelected: (selected, focused) {
                setState(() => _focusedDay = focused);
                _updateSelected(selected);
              },
              onPageChanged: (focused) => setState(() => _focusedDay = focused),
              eventLoader: _getEntriesForDay,
              calendarStyle: CalendarStyle(
                outsideDaysVisible: false,
                selectedDecoration: const BoxDecoration(color: pc, shape: BoxShape.circle),
                todayDecoration: BoxDecoration(color: pc.withOpacity(0.3), shape: BoxShape.circle),
                markerDecoration: const BoxDecoration(color: Color(0xFF00B894), shape: BoxShape.circle),
                markerSize: 5,
                markersMaxCount: 3,
                defaultTextStyle: const TextStyle(color: textDark),
                weekendTextStyle: const TextStyle(color: Color(0xFFE17055)),
                selectedTextStyle: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                todayTextStyle: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                outsideTextStyle: TextStyle(color: Colors.grey.shade300),
              ),
              headerStyle: const HeaderStyle(
                formatButtonVisible: false,
                titleCentered: true,
                titleTextStyle: TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 16),
                leftChevronIcon: Icon(Icons.chevron_left, color: textGray),
                rightChevronIcon: Icon(Icons.chevron_right, color: textGray),
              ),
              daysOfWeekStyle: const DaysOfWeekStyle(
                weekdayStyle: TextStyle(color: textGray, fontSize: 12, fontWeight: FontWeight.w600),
                weekendStyle: TextStyle(color: Color(0xFFE17055), fontSize: 12, fontWeight: FontWeight.w600),
              ),
            )),
            Divider(height: 1, color: border),

            // Selected day header
            Container(color: Colors.white, padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
              child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
                Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Text(
                    _selectedDay != null ? DateFormat('EEEE, d MMMM yyyy').format(_selectedDay!) : 'Select a date',
                    style: const TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 15)),
                  Text('${_selectedEntries.length} ${_selectedEntries.length == 1 ? 'entry' : 'entries'}',
                    style: const TextStyle(color: textGray, fontSize: 12)),
                ]),
                ElevatedButton.icon(
                  onPressed: () async {
                    final r = await Navigator.push(context, MaterialPageRoute(
                      builder: (_) => AddEntryScreen(userId: widget.userId)));
                    if (r == true) _load();
                  },
                  icon: const Icon(Icons.add, size: 16, color: Colors.white),
                  label: const Text('Add Entry', style: TextStyle(color: Colors.white, fontSize: 13)),
                  style: ElevatedButton.styleFrom(backgroundColor: pc, elevation: 0,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                    padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8))),
              ])),
            Divider(height: 1, color: border),

            // Entries for selected day
            Expanded(child: _selectedEntries.isEmpty
              ? _emptyState()
              : ListView.builder(padding: const EdgeInsets.all(16), itemCount: _selectedEntries.length,
                  itemBuilder: (_, i) => _entryCard(_selectedEntries[i]))),
          ]),
    );
  }

  Widget _emptyState() {
    return Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
      Icon(Icons.event_note_outlined, color: Colors.grey.shade300, size: 60),
      const SizedBox(height: 12),
      Text(
        _selectedDay != null && isSameDay(_selectedDay, DateTime.now())
          ? 'No entries today\nTap "Add Entry" to write!'
          : 'No entries on this date',
        textAlign: TextAlign.center,
        style: TextStyle(color: Colors.grey.shade400, fontSize: 14)),
    ]));
  }

  Widget _entryCard(DiaryEntry e) {
    final mc = _moodColors[e.mood] ?? pc;
    return Container(margin: const EdgeInsets.only(bottom: 10),
      decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(12),
        border: Border.all(color: border),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.03), blurRadius: 6, offset: const Offset(0, 2))]),
      child: Column(children: [
        Container(height: 3, decoration: BoxDecoration(color: mc, borderRadius: const BorderRadius.vertical(top: Radius.circular(12)))),
        Padding(padding: const EdgeInsets.all(14), child: Row(children: [
          Container(padding: const EdgeInsets.all(8), decoration: BoxDecoration(color: mc.withOpacity(0.1), borderRadius: BorderRadius.circular(8)),
            child: Icon(Icons.edit_note, color: mc, size: 20)),
          const SizedBox(width: 12),
          Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Row(children: [
              Expanded(child: Text(e.title, style: const TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 14), maxLines: 1, overflow: TextOverflow.ellipsis)),
              if (e.isFavorite) const Icon(Icons.favorite, color: Colors.red, size: 14),
            ]),
            const SizedBox(height: 3),
            Text(e.content, style: const TextStyle(color: textGray, fontSize: 12), maxLines: 2, overflow: TextOverflow.ellipsis),
            const SizedBox(height: 6),
            Row(children: [
              Container(padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                decoration: BoxDecoration(color: mc.withOpacity(0.1), borderRadius: BorderRadius.circular(6), border: Border.all(color: mc.withOpacity(0.3))),
                child: Text(e.mood, style: TextStyle(color: mc, fontSize: 11, fontWeight: FontWeight.w600))),
              const SizedBox(width: 6),
              Text(e.category, style: const TextStyle(color: textGray, fontSize: 11)),
            ]),
          ])),
          IconButton(icon: const Icon(Icons.arrow_forward_ios, color: textGray, size: 14),
            onPressed: () async {
              final r = await Navigator.push(context, MaterialPageRoute(
                builder: (_) => EntryDetailScreen(entry: e, userId: widget.userId)));
              if (r == 'deleted' || r == 'updated') _load();
            }, padding: EdgeInsets.zero, constraints: const BoxConstraints(minWidth: 32, minHeight: 32)),
        ])),
      ]));
  }
}
