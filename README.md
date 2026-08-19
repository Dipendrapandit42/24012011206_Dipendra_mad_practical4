# Alarm Clock Application (Practical-4)

This is an Android application developed as part of Practical-4 for Mobile Application Development. The app allows users to set and cancel alarms using Android's `AlarmManager`, `BroadcastReceiver`, and `Service` components.

## Features

- **Set Exact Alarms**: Users can pick a specific time using a `TimePickerDialog` to schedule an alarm.
- **Cancel Alarms**: Provides an easy way to cancel a scheduled alarm or stop a ringing one.
- **Foreground Service**: Uses a Foreground Service to ensure the alarm rings and plays audio even when the application is not in the foreground.
- **Notifications**: Displays a persistent notification when the alarm is active.
- **Modern Android Support**: Handles runtime permissions for notifications (Android 13+) and exact alarms (Android 12+).

## Project Structure

- **MainActivity**: The entry point of the application where users interact with the UI to set or cancel alarms.
- **AlarmBroadcastReceiver**: A `BroadcastReceiver` that listens for alarm events triggered by the system.
- **AlarmService**: A `Service` that handles audio playback and shows a foreground notification when the alarm triggers.

## Components Used

- **UI Components**: `MaterialButton`, `MaterialCardView`, `TimePickerDialog`.
- **System Services**: `AlarmManager`, `NotificationManager`.
- **Permissions**:
    - `POST_NOTIFICATIONS`: Required for showing notifications on Android 13 and above.
    - `SCHEDULE_EXACT_ALARM`: Required for scheduling precise alarms on Android 12 and above.
    - `FOREGROUND_SERVICE`: Required for running the alarm service in the foreground.

## How to Run

1. Clone the repository or open the project in Android Studio.
2. Build and run the application on an Android device or emulator.
3. Grant the necessary permissions when prompted.
4. Click on "SET ALARM" to pick a time.
5. Once the time is reached, the alarm will start ringing and a notification will appear.
6. Use the "CANCEL ALARM" button to stop the alarm.

## Developer

- **Name**: Dipendra
- **Enrollment No**: 24012011206
