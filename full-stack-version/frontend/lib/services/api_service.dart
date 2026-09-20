import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/diary_entry.dart';

class ApiService {
  // Production backend on Render
  static const String base = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'https://diary-backend-24ef.onrender.com',
  );

  static Future<Map<String, dynamic>> register(String name, String email, String password) async {
    try {
      final r = await http.post(Uri.parse('$base/api/register'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'name': name, 'email': email, 'password': password}));
      return jsonDecode(r.body);
    } catch (e) { return {'success': false, 'message': 'Cannot connect to server!'}; }
  }

  static Future<Map<String, dynamic>> login(String email, String password) async {
    try {
      final r = await http.post(Uri.parse('$base/api/login'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'email': email, 'password': password}));
      return jsonDecode(r.body);
    } catch (e) { return {'success': false, 'message': 'Cannot connect to server!'}; }
  }

  static Future<List<DiaryEntry>> getEntries(int userId) async {
    try {
      final r = await http.get(Uri.parse('$base/api/entries?userId=$userId'));
      final d = jsonDecode(r.body);
      if (d['success'] == true) return (d['entries'] as List).map((e) => DiaryEntry.fromJson(e)).toList();
      return [];
    } catch (e) { return []; }
  }

  static Future<Map<String, dynamic>> addEntry(DiaryEntry e) async {
    try {
      final r = await http.post(Uri.parse('$base/api/entries'),
        headers: {'Content-Type': 'application/json'}, body: jsonEncode(e.toJson()));
      return jsonDecode(r.body);
    } catch (e) { return {'success': false, 'message': 'Error: $e'}; }
  }

  static Future<Map<String, dynamic>> updateEntry(int id, DiaryEntry e) async {
    try {
      final r = await http.put(Uri.parse('$base/api/entries/$id'),
        headers: {'Content-Type': 'application/json'}, body: jsonEncode(e.toJson()));
      return jsonDecode(r.body);
    } catch (e) { return {'success': false, 'message': 'Error: $e'}; }
  }

  static Future<Map<String, dynamic>> deleteEntry(int id, int userId) async {
    try {
      final r = await http.delete(Uri.parse('$base/api/entries/$id?userId=$userId'));
      return jsonDecode(r.body);
    } catch (e) { return {'success': false, 'message': 'Error: $e'}; }
  }

  // ── FILE HANDLING APIS ────────────────────────────────────────────────────

  /// Export diary entries as TXT backup (returns file content)
  static Future<String?> exportTxt(int userId) async {
    try {
      final r = await http.get(Uri.parse('$base/api/export/txt?userId=$userId'));
      if (r.statusCode == 200) return r.body;
      return null;
    } catch (e) { return null; }
  }

  /// Export diary entries as CSV (returns file content)
  static Future<String?> exportCsv(int userId) async {
    try {
      final r = await http.get(Uri.parse('$base/api/export/csv?userId=$userId'));
      if (r.statusCode == 200) return r.body;
      return null;
    } catch (e) { return null; }
  }

  /// Import entries from CSV string
  static Future<Map<String, dynamic>> importCsv(int userId, String csvContent) async {
    try {
      final r = await http.post(Uri.parse('$base/api/import?userId=$userId'),
        headers: {'Content-Type': 'text/plain'}, body: csvContent);
      return jsonDecode(r.body);
    } catch (e) { return {'success': false, 'message': 'Error: $e'}; }
  }

  /// List backup files on server
  static Future<String> listBackups(int userId) async {
    try {
      final r = await http.get(Uri.parse('$base/api/backups?userId=$userId'));
      final d = jsonDecode(r.body);
      return d['backups'] ?? 'No backups found.';
    } catch (e) { return 'Cannot connect to server.'; }
  }
}