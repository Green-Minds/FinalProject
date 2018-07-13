# **Name of app**

**Name of app** shows the locations of water fountains and recycling bins on a specific campus and provides tips on stores that offer discounts to recycling.

Time spent: hours spent in total

## User Stories

* [ ] User can see a map of his area (college campus etc.) with pins at points of interest (recycling bins etc.)
* [ ] User can add a new pin
* [ ] User can choose from a list categories e.g. recycling bins, bike racks, water fountains



**required** functionality:

* [ ] Use Google Maps API
* [ ] Set up a server
* [ ] Make UI 
* [ ] Drop a pin functionality with categories 


The following **optional** features are implemented:

The following **additional** features are implemented:

* [x] User can reply from details view
* [x] Tweets favorited by logged in user have a red favorite icon

## Video Walkthrough

Here's a walkthrough of implemented user stories:

![walkthrough](./Demo.gif)  

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright [2018] [Cicely Beckford]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


1. Map
  * adding item 
    * bike racks
    * coffee shops 
    * trash bins 
    * whater fountains 
  * load item 
    * loading should be dobe based on your location
    * (optional) filtering items 
  * item information 
    * picture
    * location 
    * how many people visited that location
    * category
    * (optional) comments
    * (optional) 'report' 
  * displaying items on a map
    * google maps api
    * (optional) create clasters of items (when you have >1 item in small space)
  * (optional) routing 
  * UI
    * screen with map
      * diffrent pins (may be diffrent colors) for different categories
      * dialog should be opened when point is pressed 
        * dialog should containt
          * basic info
          * distance
          * (optional) picture
          * category 
          * button "take me there"
          * (optional) how many people visited 
    * (optional) sreen with a list ordered by distance to a point from you
    * screen to add an point 
      * fields to add information 
2. Registration Page
  * create your own userid (text field)
    * add protection from creating adentical userids 
  * schools selection (drop down list)
    * few hardcoded schools 
    * (optional) filtering
    * (optional) fetch schools from the server (some open source provider)
  * workplace (text field)
    * (optional) saving workplaces and provide them later to other people
    
    
      
      
 
   


