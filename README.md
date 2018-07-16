# **Name of app**

**Name of app** shows the locations of water fountains and recycling bins on a specific campus and provides tips on stores that offer discounts to recycling.

Time spent: hours spent in total

## User Stories

The following features are implemented:

* **0. Registration Page**
  * [ ] create your own userid (text field)
    * [ ] add protection from creating identical userids 
  * [ ] schools selection (drop down list)
    * [ ] few hardcoded schools 
    * [ ] *(optional)* filtering
    * [ ] *(optional)* fetch schools from the server (some open source provider)
  * [ ] workplace (text field)
    * [ ] *(optional)* saving workplaces and provide them later to other people
  * [ ] *(optional)* registation via facebook 
  * [ ] *(optional)* upload you picture
  
* **1. Map (view)**
   * [ ] displaying items on a map
   * [ ] google maps api
   * [ ] *(optional)* create clusters of items (when you have >1 item in small space)
   * [ ] *(optional)* routing 
   * [ ] UI
    * [ ] screen with a map
      * [ ] diffrent pins (may be diffrent colors) for different categories
       * [ ] dialog should be opened when point is pressed 
        * dialog should contain
          * basic info
          * distance
          * *(optional)* picture
          * category 
          * button "take me there"
          * *(optional)* how many people visited 
    * [ ] *(optional)* sreen with a list ordered by distance to a point from you

* **2. Map (add item)**
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

* **3. Checkin**
    * [ ] based on your location show closest item and provide a button to checkin
        * upload to the server side info that you visited that specific place
    * [ ] *(optional)* qr code scanner to indicate that you visited a place
    * [ ] *(optional)* pic regognition (of a cap or a bin etc., can be a sticker) 
    * [ ] *(optional)* AR overlay on lop of a tash bin or any other item
    * [ ] *(optional)* comment about the point 
    * [ ] *(optional)* report if not corrrect
    
* **4. Leader board**
    * [ ] list with people
        * indication of 1st, 2nd, 3ed places 
        * indicate you rank and your score 
        * iteam with should look different 
    * [ ] *(optional)* infinite scrolling / pagination
    * [ ] *(optional)* pull-to-refresh
   
* [ ] **5. (optional) Adding friends**
    * [ ] finding people by name
    * [ ] *(optional)* qr code scanner for you friends 
    
* [ ] **6. (optional) Adding goal for recycling** 

* [ ] **7. (optional) Sharing a video of you recycling**

* [ ] **8. (optional) Location based notifications if you are close to a recycling bin or coffee shop**

Server side classes:
 User:
 * username
 * password
 * checkin count
 * bottle count
 * # of made pins

 Pin:
 * LatLng
 * Category
 * # of checkins
 * Comment
 * Photo
 * User
 
 
 ## Open-source libraries used


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
