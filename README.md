# NoteManagerApp

A simple and user-friendly Android application for managing notes and categories. Features user authentication, profile management, and local data storage. Ideal for students and anyone who wants to keep their notes organized.

## Features
- User registration and login
- Create, edit, and delete notes
- Organize notes by categories
- User profile management

## Getting Started

### Prerequisites
- Android Studio (latest version recommended)
- Android device or emulator (API 24+)

### Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/NoteManagerApp.git
   ```
2. Open the project in Android Studio.
3. Let Gradle sync and download dependencies.
4. Run the app on your device or emulator.

## Project Structure
- `app/src/main/java/com/example/notemanagerapp/` — Main source code
- `app/src/main/res/` — Resources (layouts, drawables, values)
- `app/src/main/AndroidManifest.xml` — App manifest
- `build.gradle.kts`, `settings.gradle.kts` — Project configuration

## Main Activities & Fragments
- **LoginActivity** — User authentication
- **RegisterActivity** — New user registration
- **MainActivity** — Main screen with bottom navigation
  - **NotesFragment** — List and manage notes
  - **CategoriesFragment** — Organize notes by category
  - **ProfileFragment** — User profile
- **AddEditNoteActivity** — Add or edit a note
- **EditProfileActivity** — Edit user profile

## Database
The app uses a local SQLite database (see `DBHelper.kt`).

## License
_Specify your license here (e.g., MIT, Apache 2.0, etc.)_

---

_This project was created for educational purposes._
