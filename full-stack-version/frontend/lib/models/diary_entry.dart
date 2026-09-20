class DiaryEntry {
  final int id, userId;
  final String title, content, mood, category, entryDate, createdAt;
  final bool isFavorite;

  DiaryEntry({required this.id, required this.userId, required this.title,
    required this.content, required this.mood, required this.category,
    required this.isFavorite, required this.entryDate, required this.createdAt});

  factory DiaryEntry.fromJson(Map<String, dynamic> j) => DiaryEntry(
    id: j['id'] ?? 0, userId: j['userId'] ?? 0,
    title: j['title'] ?? '', content: j['content'] ?? '',
    mood: j['mood'] ?? 'Happy', category: j['category'] ?? 'General',
    isFavorite: j['isFavorite'] ?? false,
    entryDate: j['entryDate'] ?? '', createdAt: j['createdAt'] ?? '');

  Map<String, dynamic> toJson() => {'userId': userId, 'title': title,
    'content': content, 'mood': mood, 'category': category, 'isFavorite': isFavorite};
}