# Future Directions Report for SIT708 Students

The "Lost and Found App" serves as a robust starting point for exploring local data persistence and user interface management in Android. However, to transition this project into a production-grade application, several meaningful enhancements could be implemented to improve both utility and scalability.

One of the most impactful improvements would be the integration of the Google Maps SDK. Currently, locations are stored as plain text, which can be vague. By allowing users to select precise coordinates on a map and displaying all active listings as markers, the app would offer a much more intuitive and localized experience. This would require implementing Location Services and handling map overlays, which are key skills for modern mobile developers.

Furthermore, migrating from a local SQLite database to a cloud-based solution like Firebase Firestore or AWS Amplify would be essential. A cloud backend enables real-time synchronization across multiple devices and ensures that data isn't lost if a user uninstalls the app. Accompanying this with Firebase Storage would provide a more scalable way to handle high-resolution images, moving away from the limitations of local URI management.

To drive user engagement, a push notification system using Firebase Cloud Messaging (FCM) could be added. This would allow users to subscribe to specific categories, such as "Electronics" or "Pets," and receive instant alerts when a matching item is posted. Additionally, implementing a user authentication system would allow for personal profiles where users can manage their own posts, mark items as "Resolved" rather than simply deleting them, and even message other users securely.

Finally, exploring machine learning via Google’s ML Kit could automate item categorization based on uploaded photos. These advancements would not only make the app more professional but also align with the advanced learning objectives of the SIT708 unit by challenging students to master asynchronous programming, cloud architecture, and secure data handling.
