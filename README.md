# Lost and Found App

## Overview
The Lost and Found App is a simple Android application developed in Java that allows users to post advertisements for items they have lost or found. Other users can browse these listings, filter them by category, and see detailed information including an image of the item. Once an item is returned, the post can be removed from the system.

## Features
- **Home Screen**: Navigation to create a new advert or view all items.
- **Create Advert**:
    - Form to input Name, Phone, Description, Date, and Location.
    - Post type selection (Lost or Found).
    - Category selection from a dropdown (Electronics, Pets, Wallets, etc.).
    - Mandatory Image Upload from the device gallery.
    - Automatic timestamp recording.
- **Listings Screen**:
    - View all items in a list.
    - Filter items by category using a dropdown.
    - Quick view of basic item details.
- **Details Screen**:
    - View full details including the uploaded image.
    - Remove the advert once the item is recovered.

## Technologies Used
- **Language**: Java
- **Database**: SQLite (local storage)
- **UI**: XML Layouts
- **Image Handling**: `ActivityResultLauncher` for gallery selection.

## How to Run the App
1. Open the project in Android Studio.
2. Ensure you are using an Android device or emulator (API 24+ recommended).
3. Build and run the app.
4. On the first run, grant any requested storage permissions for image selection.

## Implementation Details
- **SQLite**: A `DatabaseHelper` class manages the creation of the `adverts` table and handles CRUD operations (Insert, Query, Delete).
- **Image Upload**: The app uses `ActivityResultContracts.GetContent()` to let users pick an image. The resulting Uri is stored as a string in the database.
- **Category Filtering**: A Spinner on the list screen triggers a specific SQL query (`SELECT * FROM adverts WHERE category = ?`) to refresh the list view.

## LLM Declaration
I declare that a Large Language Model (LLM) was used to help generate the initial project scaffolding, basic SQLite helper methods, and boilerplate XML layouts. Following this initial setup, I personally implemented all key functional enhancements, including the logic for category filtering and the automatic timestamping. I also independently diagnosed and resolved a significant technical hurdle regarding image access permissions by developing a custom internal storage system for uploaded photos. Furthermore, I refined the user interface for improved usability and added consistent navigation elements, such as action bar back buttons, to ensure the final application operates reliably and meets all project requirements.
