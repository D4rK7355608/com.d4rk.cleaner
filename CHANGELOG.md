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