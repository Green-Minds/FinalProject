# **Cycle**

**Cycle** shows the locations of water fountains and recycling bins on a specific campus and provides tips on stores that offer discounts to recycling.

Time spent: hours spent in total

## User Stories

The following features are implemented:

* **0. Registration Page**
  * [x] create your own userid (text field)
    * [x] add protection from creating identical userids 
  * [x] schools selection (drop down list)
    * [x] *(optional)* filtering
    * [x] *(optional)* fetch schools from the server (some open source provider)
  * [x] workplace (text field)
    * [ ] *(optional)* saving workplaces and provide them later to other people
  * [x] *(optional)* upload image or use app's camera

* **1. Login Page**
  * [x] *(optional)* login via facebook 
    * [x] register via facebook and get user's information from facebook
  * [x] user persistence with facebook and parse login

  
* **2. Map (view)**
   * [x] displaying items on a map
   * [x] google maps api
   * [x] *(optional)* create clusters of items (when you have >1 item in small space)
   * [ ] *(optional)* routing 
   * [x] UI
    * [x] screen with a map
      * [x] diffrent pins (may be diffrent colors) for different categories
       * [ ] dialog should be opened when point is pressed 
        * dialog should contain
          * basic info
          * distance
          * *(optional)* picture
          * category 
          * button "take me there"
          * *(optional)* how many people visited 
    * [ ] *(optional)* screen with a list ordered by distance to a point from you

* **3. Map (add item)**
  * [ ] adding item 
    * bike racks
    * coffee shops 
    * trash bins 
    * water fountains 
  * [ ] load item 
    * loading should be done based on your location
    * *(optional)* filtering items 
  * [ ] item information 
    * picture
    * location 
    * how many people visited that location
    * category
    * (optional) comments
    * (optional) 'report' 
  * [ ] UI   
    * screen to add an point 
    * fields to add information 
    
    
    https://developers.google.com/maps/documentation/android-sdk/marker

* **4. Checkin**
    * [ ] based on your location show closest item and provide a button to checkin
        * upload to the server side info that you visited that specific place
    * [ ] *(optional)* qr code scanner to indicate that you visited a place
    * [ ] *(optional)* pic regognition (of a cap or a bin etc., can be a sticker) 
    * [ ] *(optional)* AR overlay on lop of a tash bin or any other item
    * [ ] *(optional)* comment about the point 
    * [ ] *(optional)* report if not corrrect
    
* **5. Leader board**
    * [x] leaderboard based on connection (school or work)
       [x] * indication of 1st, 2nd, 3rd places 
       [x] * indicate your rank and your score 
       [x] * item with your info should look different 
    * [ ] *(optional)* infinite scrolling / pagination
    * [x] *(optional)* pull-to-refresh
   
* [ ] **6. (optional) Adding friends**
    * [ ] finding people by name
    * [ ] *(optional)* qr code scanner for you friends 
    
* [ ] **7. (optional) Adding goal for recycling** 

* [ ] **8. (optional) Sharing a video of you recycling**

* [ ] **8. (optional) Location based notifications if you are close to a recycling bin or coffee shop**

Server side classes:
 User:
 * username
 * profile image
 * password
 * checkin count
 * bottle count
 * number of made pins

 Pin:
 * LatLng
 * Category
 * number of checkins
 * Comment
 * Photo
 * User
 
 
## Notes

Describe any challenges encountered while building the app.

## Open-source libraries used

- [US Colleges API](https://github.com/karllhughes/colleges) - API for accessing the names and locations of accredited colleges and universities in the United States
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright [2018] [Green Minds]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
