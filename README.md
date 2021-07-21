# Veggie

Veggie is an android app for supermarkets. With this app, they will attract potential customers by offering them a simple and fast way to shop fruits and vegetables from anywhere at anytime. The customer just selects products, delivery date-time and receive their order at home.

#### Note:
This app is currently published as beta version in Play Store in the following `<link>` : <https://play.google.com/store/apps/details?id=com.diegoparra.veggie>


## Screenshots

<img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/products.jpeg?alt=media&token=4f0aceeb-dd5c-4433-8add-179a59d6ed62" alt="products" width="150"/> &nbsp; <img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/product_details.jpeg?alt=media&token=7ef65ed7-c5af-46a4-87d8-56cdf8a1dde9" alt="product_details" width="150"/> &nbsp; <img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/products_dark_no_internet.jpeg?alt=media&token=89c4cfd6-4506-40f6-a5fb-79bbd71be8bf" alt="products_dark_no_internet" width="150"/> &nbsp; <img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/product_details_dark.jpeg?alt=media&token=e243304f-cb72-4779-a357-7e904e6aa5e2" alt="product_details_dark" width="150"/> &nbsp; <img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/search.jpeg?alt=media&token=9d992153-a3bb-47c1-a23f-5dda539a6d24" alt="search" width="150"/>

<img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/cart.jpeg?alt=media&token=e5e1474d-dd12-4261-b33d-7e945849d44d" alt="cart" width="150" /> &nbsp; <img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/order1.jpeg?alt=media&token=9709eb65-349c-4a92-8ecf-0dd8d7d4b045" alt="order1" width="150"/> &nbsp; <img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/order_send.jpeg?alt=media&token=feadadb8-24d5-41a0-b50b-f8b78ed141bf" alt="order2" width="150"/> &nbsp; <img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/user_orders.jpeg?alt=media&token=2b8e41a6-12d5-4205-8870-3fc626ff6eac" alt="user_orders" width="150"/>

<img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/sign_in.jpeg?alt=media&token=9f5720a6-022e-4d0e-aed4-aa65c258e662" alt="sign_in" width="150"/> &nbsp; <img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/google_sign_in.jpeg?alt=media&token=66653d8b-7c9b-457f-bf1a-659a928e4346" alt="google_sign_in" width="150"/> &nbsp; <img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/phone_auth.jpeg?alt=media&token=02bc9224-763b-4933-96ab-d5539bb92878" alt="phone_verification" width="150"/> &nbsp; <img src="https://firebasestorage.googleapis.com/v0/b/veggie-co.appspot.com/o/support.jpeg?alt=media&token=00946b15-d0c4-439b-87a6-814a6ec46bae" alt="support" width="150"/>


## Features

- App works in offline mode
- Light/Dark Theme
- Products list / details & search
- Users/Accounts - Email/Google/Facebook Sign in
- Phone number verification
- Notifications

## Frameworks / Libraries

- Kotlin
- MVVM and clean architecture
- Dependency injection: Dagger-Hilt
- Kotlin coroutines & flows
- Data persistence: Room & DataStore
- Navigation component
- Firebase (Firestore, auth, notifications, remoteConfig)
- Retrofit & Gson (In the retrofit branch)
- viewBinding
- Coil
- Custom views
- Material Design

## Some additional notes

Some relevant features I have also added in the code may include:

- Reduced calls to firebase and documents fetched, that way costs can be reduced:
  - Developer can set a minimum fetch interval, for example 5 min for products, and if that time has not been reached documents will be queried from local database avoiding collecting data from firebase.
  - Every time products collection is called, app will compare the last updated time in local database and fetch documents that were updated in firestore after that datetime.
- In order to implement Retrofit, I used the realtime database which can work as a REST Api.
- App can be used either in protrait or landscape mode without problems.
- App is able to check internet connection by using network callbacks, checking internet access and checking captive portals.
- Products and auth were built up in modules so that they can be reused in the admin app.
- Resources are well organized so that it is easy perform changes like app font, colors, translations, theme, etc.


## Future work

- The main purpose of this app was to apply knowledge in different frameworks and skills, but I have also striven to make this app functional to a supermarket. In that sense, this app has full basic functionality to a user: they can select products, manage a shopping cart, make and track the order. And, in the admin side, they can also manage the app, check users, track orders, send notifications, etc. from the firebase console.
- Some future work may include making an app for the admin where they can manage easier and faster the information, referral programs, programs for loyal customers, beautify some layouts, etc.


## License

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
