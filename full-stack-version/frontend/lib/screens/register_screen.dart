import 'package:flutter/material.dart';
import '../services/api_service.dart';
import 'login_screen.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});
  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _nameCtrl = TextEditingController();
  final _emailCtrl = TextEditingController();
  final _passCtrl = TextEditingController();
  bool _loading = false;
  String _error = '';
  String _success = '';
  static const Color pc = Color(0xFF6C5CE7);
  static const Color bg = Color(0xFFF5F6FA);
  static const Color textDark = Color(0xFF2D3436);
  static const Color textGray = Color(0xFF636E72);

  Future<void> _register() async {
    if (_nameCtrl.text.isEmpty || _emailCtrl.text.isEmpty || _passCtrl.text.isEmpty) {
      setState(() => _error = 'Please fill all fields'); return;
    }
    if (_passCtrl.text.trim().length < 6) {
      setState(() => _error = 'Password must be at least 6 characters'); return;
    }
    setState(() { _loading = true; _error = ''; _success = ''; });
    final r = await ApiService.register(_nameCtrl.text.trim(), _emailCtrl.text.trim(), _passCtrl.text.trim());
    setState(() => _loading = false);
    if (r['success'] == true) {
      setState(() => _success = 'Account created! Redirecting to login...');
      await Future.delayed(const Duration(seconds: 2));
      if (mounted) Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const LoginScreen()));
    } else {
      setState(() => _error = r['message'] ?? 'Registration failed');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(backgroundColor: bg,
      body: Center(child: SingleChildScrollView(padding: const EdgeInsets.all(32),
        child: ConstrainedBox(constraints: const BoxConstraints(maxWidth: 420),
          child: Column(children: [
            Container(padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(color: pc.withOpacity(0.1), shape: BoxShape.circle),
              child: const Icon(Icons.book, color: pc, size: 52)),
            const SizedBox(height: 16),
            const Text('Create Account', style: TextStyle(color: textDark, fontSize: 28, fontWeight: FontWeight.bold)),
            const SizedBox(height: 4),
            const Text('Start your diary journey', style: TextStyle(color: textGray, fontSize: 14)),
            const SizedBox(height: 36),
            Container(padding: const EdgeInsets.all(28), decoration: BoxDecoration(
              color: Colors.white, borderRadius: BorderRadius.circular(16),
              boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.06), blurRadius: 20, offset: const Offset(0, 4))]),
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                _label('Full Name'), const SizedBox(height: 6),
                _field(_nameCtrl, 'Akhilesh Yadav', Icons.person_outline),
                const SizedBox(height: 14),
                _label('Email Address'), const SizedBox(height: 6),
                _field(_emailCtrl, 'you@email.com', Icons.email_outlined),
                const SizedBox(height: 14),
                _label('Password'), const SizedBox(height: 6),
                _field(_passCtrl, 'Minimum 6 characters', Icons.lock_outline, obscure: true),
                if (_error.isNotEmpty) ...[const SizedBox(height: 12),
                  Container(padding: const EdgeInsets.all(10), decoration: BoxDecoration(
                    color: Colors.red.shade50, borderRadius: BorderRadius.circular(8), border: Border.all(color: Colors.red.shade200)),
                    child: Row(children: [Icon(Icons.error_outline, color: Colors.red.shade400, size: 16), const SizedBox(width: 8),
                      Expanded(child: Text(_error, style: TextStyle(color: Colors.red.shade700, fontSize: 13)))]))],
                if (_success.isNotEmpty) ...[const SizedBox(height: 12),
                  Container(padding: const EdgeInsets.all(10), decoration: BoxDecoration(
                    color: Colors.green.shade50, borderRadius: BorderRadius.circular(8), border: Border.all(color: Colors.green.shade200)),
                    child: Row(children: [Icon(Icons.check_circle_outline, color: Colors.green.shade600, size: 16), const SizedBox(width: 8),
                      Expanded(child: Text(_success, style: TextStyle(color: Colors.green.shade700, fontSize: 13)))]))],
                const SizedBox(height: 24),
                SizedBox(width: double.infinity, height: 48,
                  child: ElevatedButton(onPressed: _loading ? null : _register,
                    style: ElevatedButton.styleFrom(backgroundColor: pc, foregroundColor: Colors.white, elevation: 0,
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10))),
                    child: _loading ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                        : const Text('Create Account', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)))),
                const SizedBox(height: 20),
                Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                  const Text('Already have an account? ', style: TextStyle(color: textGray, fontSize: 13)),
                  GestureDetector(onTap: () => Navigator.pop(context),
                    child: const Text('Sign In', style: TextStyle(color: pc, fontWeight: FontWeight.bold, fontSize: 13))),
                ]),
              ])),
          ])))));
  }

  Widget _label(String t) => Text(t, style: const TextStyle(color: textDark, fontSize: 13, fontWeight: FontWeight.w600));
  Widget _field(TextEditingController c, String hint, IconData icon, {bool obscure=false}) =>
    TextField(controller: c, obscureText: obscure, style: const TextStyle(color: textDark, fontSize: 14),
      decoration: InputDecoration(hintText: hint, hintStyle: const TextStyle(color: textGray),
        prefixIcon: Icon(icon, color: textGray, size: 18), filled: true, fillColor: const Color(0xFFF8F9FA),
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: const BorderSide(color: Color(0xFFDFE6E9))),
        enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: const BorderSide(color: Color(0xFFDFE6E9))),
        focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: const BorderSide(color: pc, width: 1.5)),
        contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12)));
}