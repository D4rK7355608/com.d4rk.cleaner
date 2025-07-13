# Version 3.5.0:

- **New**: Implemented an automatic scheduling feature for daily cleaning routines.
- **New**: Introduced functionality to suggest uninstallation of rarely used applications, utilizing UsageStats for analysis.
- **New**: Enhanced the "Analyze" tabs screen with the capability to recognize and identify large files.
- **Minor**: Refined animations within the scanner screen for a smoother user experience.

# Version 3.4.0:

- **Major**: Added a new WhatsApp Cleaner screen to preview, sort, and delete your WhatsApp media.
- **Major**: Added a Clipboard Cleaner item in the Scanner screen with a quick shortcut to clear your clipboard.
- **Minor**: The "Home" tab is now called "Scanner" and has a new icon, making it clearer what this section is for.
- **Minor**: Cleanup reminder notifications are now available in more languages.
- **Minor**: We've removed some unused code related to app permissions for better efficiency.
- **Minor**: File sizes and memory usage are now formatted more accurately and consistently.
- **Minor**: Applied visual polish and consistency fixes throughout the app.
- **Patch**: The app now checks if it has permission to send notifications before trying to send one for cleanup reminders. This prevents potential crashes.
- **Patch**: Various bug fixes and performance improvements to keep the app running smoothly.

# Version 3.3.0:

- **New**: We've added a helpful setup guide and made how we ask for your permission clearer.
- **New**: Now you can see a small preview of your PDF files.
- **Patch**: Fixed how file cards look when showing duplicate files.
- **Patch**: Solved some behind-the-scenes issues with how files are grouped.
- **Minor**: Improved loading animations for a nicer look.
- **Minor**: Easily find and remove duplicate files in the new "Duplicates" section.
- **Patch**: Many under-the-hood improvements for better performance and stability.

# Version 3.2.2:

- **Minor**: Under-the-hood improvements for better performance and stability.

# Version 3.2.1:

- **Patch**: Fixed an issue where the Manual mode fields and slider did not reset to their default (original) values when switching from other tabs.
- **Patch**: Ensured that settings in Quick Compress and File Size remain independent from those in Manual mode.
- **Minor**: Improved the Manual mode so that the preview updates automatically when the user finishes editing the width and height fields—now it updates after a brief pause (400 ms) or when “Done” is pressed.
- **Minor**: Under-the-hood improvements for better performance and stability.

# Version 3.2.0:

- **New**: Added better support for tablets and large screens.
- **Major**: Improved file grouping and sorting for better clarity.
- **Major**: Enhanced file scanning to detect and organize more file types, like fonts and Windows files.
- **Minor**: Redesigned the help page for improved usability.
- **Minor**: Updated splash screen animation and app icon.
- **Patch**: Fixed several bugs and optimized resource handling.
- **Patch**: Updated all libraries to improve compatibility and performance.
- **Patch**: Many under-the-hood improvements for better performance and stability.

# Version 3.1.0:

- **New**: Added a refreshed About Libraries screen with an improved design and information.
- **New**: Added Thai and Brazilian Portuguese language support.
- **New**: The app list now dynamically updates when uninstalling apps, eliminating the need for a
  full list reload, resulting in a smoother user experience.
- **Minor**: Updated project dependencies to improve user experience and streamline the development
  process.
- **Minor**: Floating action buttons (FABs) have subtle entry animations for a smoother visual flow.
- **Minor**: Removed unused project dependencies to reduce build size and potential conflicts.
- **Minor**: Implemented various under-the-hood optimizations to enhance performance and stability.
- **Minor**: Removed unused project dependencies to reduce build size and potential conflicts.
- **Minor**: Removed unused resources to further optimize the application.
- **Patch**: Corrected the privacy settings title from "Settings" to its proper name.
- **Patch**: Resolved an issue with the Traditional Chinese language toggle.
- **Patch**: Fixed a bug where users could repeatedly reload the same screen in the bottom
  navigation bar.
- **Patch**:Fixed an issue where the app icon appeared incorrectly shaped on older devices.
- **Patch**: Prevented users from navigating back to the onboarding screen after accepting the terms
  and conditions on a fresh install.

# Version 3.0.0:

- **New**: Completely redesigned the Home and Analyze screens for a more intuitive and visually
  appealing user interface.
- **New**: Implemented date-based filtering in the Analyze screen, allowing for easier file
  selection and management.
- **New**: Introduced loading indicators in the App Manager for a smoother and more informative
  experience.
- **New**: Added messages to indicate when no apps or APKs are found.
- **New**: Enabled real-time RAM usage monitoring in the Memory Manager.
- **New**: A new Trash screen has been added to provide more granular control over your device's
  storage.
- **New**: Added a loading animation to the Memory Manager for visual feedback during data
  retrieval.
- **New**: Introduced a new setting to customize the app's startup page, providing users with
  greater control over their initial experience.
- **New**: Added an option to disable the bounce click effect for users who prefer a more
  traditional interaction style.
- **New**: Added a snackbar notification for older Android versions when users copy device
  information from the About section.
- **New**: Integrated haptic feedback for swipe gestures, enhancing the tactile response and user
  experience.
- **New**: Implemented sound effects on tap interactions for a more engaging experience.
- **New**: Redesigned the Settings page, aligning it with the modern aesthetics of the Android 15
  design system.
- **Major**: Restructured the app's code flow for improved organization and future extensibility.
- **Major**: Implemented a robust error handling mechanism to gracefully manage unexpected
  situations and provide helpful feedback to users.
- **Minor**: Backported the app to support devices running Android 6.0 and above.
- **Patch**: Fixed visual glitches within the App Manager.
- **Patch**: Optimized app loading speed in the App Manager through code refactoring and efficiency
  improvements.
- **Patch**: Resolved an issue where APK installation from the App Manager was not functioning
  correctly.
- **Patch**: Improved the "Select All" button behavior in the App Manager to ensure accurate state
  representation.
- **Patch**: Resolved an issue where language selection was not consistently applied.

# Version 2.0.0:

- **New**: Added a progress bar to the main screen, offering an approximate visualization of storage
  usage.
- **New**: Users can now select specific files for deletion after the scan completes, allowing for
  granular control.
- **New**: Enhanced the post-scan screen to display previews of images and videos, aiding in file
  selection.
- **New**: Introduced the option to select all files for deletion, streamlining the cleaning
  process.
- **New**: Completely overhauled the memory manager, now showcasing storage usage categorized by
  file types.
- **New**: Added support for dynamic colors on compatible devices, allowing the app to adapt to
  system-wide color palettes.
- **New**: Refined the AMOLED theme for a more immersive dark mode experience.
- **New**: Incorporated updated translations thanks to valuable contributions from the community.
- **New**: Introduced a dedicated section for managing security and privacy settings within the app.
- **New**: Implemented new animations and improved overall app responsiveness for a smoother user
  experience.
- **Major**: Migrated the entire app to Jetpack Compose, providing a modern and improved user
  interface.
- **Major**: Completely reworked the app's logic using view models and coroutines for enhanced
  performance and maintainability.

# Version 1.1.0:

- **Minor**: Added GitHub issues templates.
- **Minor**: Updated project dependencies.
- **Minor**: Improved the user experience in Help and Feedback page.
- **Minor**: Made minor under-the-hood improvements for a better overall app experience.
- **Patch**: Fixed an issue that caused the app to crash when the device was rotated during the
  cleaning or scanning process.
- **Patch**: Fixed an issue encountered on Android 14 where the application was requesting photo
  permissions and initiating the photo picker API.
- **Patch**: Fixed an issue in the Image Optimizer that led to a crash when no gallery application
  was installed on the user’s device.

# Version 1.0.0:

- **New**: Added multiple languages support for the app.
- **New**: Added a new bottom app bar for the main components of the cleaner to be more accessible
  for the user.
- **New**: Added legal notices and more information about permissions.
- **New**: Added a bug report feature to report bugs on GitHub.
- **New**: Added many display customizations for the app.
- **New**: Added a new GDPR message to comply with Google Play policy.
- **New**: Added new custom startup animations.
- **New**: Added support for AMOLED themes.
- **New**: APK sharing functionality has been improved to ensure the proper sharing of APK files.
- **Major**: Migrated the app to Semantic Versioning (SemVer).
- **Major**: Reworked the settings page and organized it way better.
- **Major**: Reworked the file list menu, providing a more intuitive and efficient user experience.
- **Major**: Reworked the Image Optimizer by adding quick compression, manual compression and file
  compression.
- **Minor**: Reset the version to 1.0.0 for a fresh start.
- **Minor**: Replaced toasts with snack bars for all notifications.
- **Minor**: Addressed an aesthetic concern by optimizing the display of the temperature icon when
  the application operates in dark mode.
- **Patch**: Rectified an issue where the uninstall item was inappropriately located within both the
  APKs and System apps tabs, ensuring a more streamlined and intuitive user experience.
- **Patch**: Improved the ads initialization and loading for a better user experience.
- **Patch**: Improved permissions handling logic for improved security and user experience.
- **Patch**: Improved the app's logical parent activities.
- **Patch**: Fixed daily clean running when is turned off from settings.
- **Patch**: Fixed memory manager crashes when trying to exit and enter.
- **Patch**: Fixed the whitelist save issue.
- **Patch**: Fixed uninstall option from the App Manager.
- **Patch**: Fixed app crashes caused by the image optimizer.
- **Patch**: Made various under-the-hood improvements for a better overall app experience.