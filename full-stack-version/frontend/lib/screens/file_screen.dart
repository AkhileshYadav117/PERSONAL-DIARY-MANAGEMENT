import 'package:flutter/material.dart';
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;
import '../services/api_service.dart';

class FileScreen extends StatefulWidget {
  final int userId;
  const FileScreen({super.key, required this.userId});
  @override
  State<FileScreen> createState() => _FileScreenState();
}

class _FileScreenState extends State<FileScreen> {
  bool _loadingTxt = false;
  bool _loadingCsv = false;
  bool _loadingImport = false;
  bool _loadingBackups = false;
  String _backupList = '';
  String _status = '';
  bool _statusOk = true;

  static const Color pc = Color(0xFF6C5CE7);
  static const Color bg = Color(0xFFF5F6FA);
  static const Color textDark = Color(0xFF2D3436);
  static const Color textGray = Color(0xFF636E72);
  static const Color border = Color(0xFFDFE6E9);

  // ── Download TXT Backup ────────────────────────────────────────────────────
  Future<void> _downloadTxt() async {
    setState(() { _loadingTxt = true; _status = ''; });
    final content = await ApiService.exportTxt(widget.userId);
    setState(() => _loadingTxt = false);
    if (content != null) {
      _triggerDownload(content, 'diary_backup.txt', 'text/plain');
      _setStatus('✅ Backup downloaded successfully!', true);
    } else {
      _setStatus('❌ Failed to create backup. Is backend running?', false);
    }
  }

  // ── Download CSV Export ────────────────────────────────────────────────────
  Future<void> _downloadCsv() async {
    setState(() { _loadingCsv = true; _status = ''; });
    final content = await ApiService.exportCsv(widget.userId);
    setState(() => _loadingCsv = false);
    if (content != null) {
      _triggerDownload(content, 'diary_export.csv', 'text/csv');
      _setStatus('✅ CSV exported successfully! Open with Excel.', true);
    } else {
      _setStatus('❌ Failed to export CSV.', false);
    }
  }

  // ── Import from CSV ────────────────────────────────────────────────────────
  Future<void> _importCsv() async {
    final input = html.FileUploadInputElement()..accept = '.csv';
    input.click();
    input.onChange.listen((event) async {
      final file = input.files?.first;
      if (file == null) return;
      setState(() { _loadingImport = true; _status = ''; });
      final reader = html.FileReader();
      reader.readAsText(file);
      reader.onLoad.listen((_) async {
        final csvContent = reader.result as String;
        final result = await ApiService.importCsv(widget.userId, csvContent);
        setState(() => _loadingImport = false);
        if (result['success'] == true) {
          _setStatus('✅ ${result['message']}', true);
        } else {
          _setStatus('❌ Import failed: ${result['message']}', false);
        }
      });
    });
  }

  // ── List Backups ───────────────────────────────────────────────────────────
  Future<void> _loadBackups() async {
    setState(() { _loadingBackups = true; _backupList = ''; });
    final list = await ApiService.listBackups(widget.userId);
    setState(() { _backupList = list; _loadingBackups = false; });
  }

  // ── Browser Download Trigger ───────────────────────────────────────────────
  void _triggerDownload(String content, String filename, String mimeType) {
    final bytes = html.Blob([content], mimeType);
    final url = html.Url.createObjectUrlFromBlob(bytes);
    final anchor = html.AnchorElement(href: url)
      ..setAttribute('download', filename)
      ..click();
    html.Url.revokeObjectUrl(url);
  }

  void _setStatus(String msg, bool ok) {
    setState(() { _status = msg; _statusOk = ok; });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(backgroundColor: bg,
      appBar: AppBar(backgroundColor: Colors.white, elevation: 0,
        surfaceTintColor: Colors.transparent,
        bottom: PreferredSize(preferredSize: const Size.fromHeight(1), child: Divider(height: 1, color: border)),
        leading: IconButton(icon: const Icon(Icons.arrow_back, color: textDark), onPressed: () => Navigator.pop(context)),
        title: const Text('File Handling', style: TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 17))),
      body: SingleChildScrollView(padding: const EdgeInsets.all(16), child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [

        // Status message
        if (_status.isNotEmpty)
          Container(margin: const EdgeInsets.only(bottom: 12), padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: _statusOk ? Colors.green.shade50 : Colors.red.shade50,
              borderRadius: BorderRadius.circular(10),
              border: Border.all(color: _statusOk ? Colors.green.shade200 : Colors.red.shade200)),
            child: Row(children: [
              Icon(_statusOk ? Icons.check_circle_outline : Icons.error_outline,
                color: _statusOk ? Colors.green.shade700 : Colors.red.shade700, size: 18),
              const SizedBox(width: 8),
              Expanded(child: Text(_status,
                style: TextStyle(color: _statusOk ? Colors.green.shade700 : Colors.red.shade700, fontSize: 13))),
            ])),

        // EXPORT section
        _sectionTitle('📤 Export / Backup', 'Save your diary entries to a file'),
        const SizedBox(height: 12),
        Row(children: [
          Expanded(child: _actionCard(
            icon: Icons.description_outlined,
            title: 'Backup as TXT',
            subtitle: 'Human-readable diary backup',
            color: pc,
            loading: _loadingTxt,
            onTap: _downloadTxt,
          )),
          const SizedBox(width: 12),
          Expanded(child: _actionCard(
            icon: Icons.table_chart_outlined,
            title: 'Export as CSV',
            subtitle: 'Open with Excel or Sheets',
            color: const Color(0xFF00B894),
            loading: _loadingCsv,
            onTap: _downloadCsv,
          )),
        ]),

        const SizedBox(height: 20),

        // IMPORT section
        _sectionTitle('📥 Import', 'Load diary entries from a CSV file'),
        const SizedBox(height: 12),
        _actionCard(
          icon: Icons.upload_file_outlined,
          title: 'Import from CSV',
          subtitle: 'Select a previously exported CSV file to import entries',
          color: const Color(0xFFE17055),
          loading: _loadingImport,
          onTap: _importCsv,
          fullWidth: true,
        ),
        const SizedBox(height: 8),
        Container(padding: const EdgeInsets.all(12), decoration: BoxDecoration(
          color: Colors.amber.shade50, borderRadius: BorderRadius.circular(8),
          border: Border.all(color: Colors.amber.shade200)),
          child: Row(children: [
            Icon(Icons.info_outline, color: Colors.amber.shade700, size: 16),
            const SizedBox(width: 8),
            const Expanded(child: Text('Import only works with CSV files exported from this app.',
              style: TextStyle(color: Color(0xFF795548), fontSize: 12))),
          ])),

        const SizedBox(height: 20),

        // BACKUPS section
        _sectionTitle('🗂️ Saved Backups', 'Files saved on the server'),
        const SizedBox(height: 12),
        Container(width: double.infinity, padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(12), border: Border.all(color: border),
            boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.03), blurRadius: 8, offset: const Offset(0, 2))]),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
              const Text('Backup Files', style: TextStyle(color: textDark, fontWeight: FontWeight.w600, fontSize: 14)),
              TextButton.icon(onPressed: _loadingBackups ? null : _loadBackups,
                icon: _loadingBackups
                  ? const SizedBox(width: 14, height: 14, child: CircularProgressIndicator(strokeWidth: 2, color: pc))
                  : const Icon(Icons.refresh, color: pc, size: 16),
                label: const Text('Refresh', style: TextStyle(color: pc, fontSize: 13))),
            ]),
            const Divider(),
            _backupList.isEmpty
              ? const Text('Click Refresh to see saved backup files.', style: TextStyle(color: textGray, fontSize: 13))
              : Text(_backupList, style: const TextStyle(color: textDark, fontSize: 13, height: 1.6)),
          ])),

        const SizedBox(height: 20),

        // Info card
        Container(padding: const EdgeInsets.all(16), decoration: BoxDecoration(
          color: pc.withOpacity(0.05), borderRadius: BorderRadius.circular(12),
          border: Border.all(color: pc.withOpacity(0.2))),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Row(children: [
              Icon(Icons.folder_outlined, color: pc, size: 18),
              const SizedBox(width: 8),
              const Text('About File Handling', style: TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 14)),
            ]),
            const SizedBox(height: 8),
            const Text(
              '• TXT Backup: Saves all entries to a human-readable text file\n'
              '• CSV Export: Creates a spreadsheet-compatible file\n'
              '• Import: Reads a CSV file and adds entries to database\n'
              '• Backups are saved in the "backups/" folder on the server\n'
              '• Uses Java File I/O: FileWriter, BufferedWriter, BufferedReader',
              style: TextStyle(color: textGray, fontSize: 12, height: 1.7)),
          ])),

        const SizedBox(height: 30),
      ])));
  }

  Widget _sectionTitle(String title, String subtitle) {
    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Text(title, style: const TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 16)),
      const SizedBox(height: 2),
      Text(subtitle, style: const TextStyle(color: textGray, fontSize: 12)),
    ]);
  }

  Widget _actionCard({
    required IconData icon,
    required String title,
    required String subtitle,
    required Color color,
    required bool loading,
    required VoidCallback onTap,
    bool fullWidth = false,
  }) {
    return GestureDetector(onTap: loading ? null : onTap,
      child: Container(width: fullWidth ? double.infinity : null,
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(12),
          border: Border.all(color: border),
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.03), blurRadius: 8, offset: const Offset(0, 2))]),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(children: [
            Container(padding: const EdgeInsets.all(8), decoration: BoxDecoration(color: color.withOpacity(0.1), borderRadius: BorderRadius.circular(8)),
              child: loading
                ? SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2, color: color))
                : Icon(icon, color: color, size: 20)),
            if (fullWidth) ...[const SizedBox(width: 12), Expanded(child: Text(title, style: const TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 14)))],
          ]),
          if (!fullWidth) ...[const SizedBox(height: 10), Text(title, style: const TextStyle(color: textDark, fontWeight: FontWeight.bold, fontSize: 13))],
          const SizedBox(height: 4),
          Text(subtitle, style: const TextStyle(color: textGray, fontSize: 11), maxLines: 2),
          const SizedBox(height: 10),
          Container(padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
            decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(8)),
            child: Text(loading ? 'Processing...' : 'Click to ${title.split(' ')[0]}',
              style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.w600))),
        ])));
  }
}
