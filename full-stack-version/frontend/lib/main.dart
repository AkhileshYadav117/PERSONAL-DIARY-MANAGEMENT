import 'package:flutter/material.dart';
import 'screens/login_screen.dart';

void main() { runApp(const PersonalDiaryApp()); }

class PersonalDiaryApp extends StatelessWidget {
  const PersonalDiaryApp({super.key});
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Personal Diary',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        brightness: Brightness.light,
        primaryColor: const Color(0xFF6C5CE7),
        scaffoldBackgroundColor: const Color(0xFFF5F6FA),
        colorScheme: ColorScheme.light(primary: const Color(0xFF6C5CE7), surface: Colors.white),
        fontFamily: 'Roboto',
      ),
      home: const LoginScreen(),
    );
  }
}