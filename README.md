# CoCo (CollegeConnect)
An app for high schoolers to learn about their dream colleges, directly from college students.

## Table of Contents
1. [Demo Link](#demo-link)
1. [Overview](#overview)
2. [App Screenshots](#app-screenshots)
3. [Product Spec](#product-spec)
4. [Schema](#schema)

## [Demo Link](https://drive.google.com/file/d/1Cl01HXFAPE3ZeoFknO5-DXPAm4MRE_79/view?usp=sharing)  
You may have to download the video to view it.

## Overview
### Description
This app allows high schoolers to learn about their dream colleges. They can filter for schools, save and organize schools into their safety/match/reach lists, view photo albums created by college students, message/call/share a meetup location with currently enrolled college students, and more. 


### App Evaluation
- **Category:** Social, education
- **Mobile:** Mobile experience allows for instant messaging, push notifications, and potentially location sharing. 
- **Story:** Encourages high school students to be more proactive in their college search by reaching out to older college students who have been in their shoes before. Encourages dialogue and media sharing between real students that contrasts official university-curated marketing. 
- **Market:** High school students (juniors, seniors) who wish to make good college choices, college students who want to help younger students and share their experiences
- **Habit:** High school juniors and seniors especially would use this heavily during application season. College students would respond to messages accordingly and also upload school photos year-round

## App Screenshots
A few screenshots highlighting the feature's apps. All features are shown in the [demo](https://drive.google.com/file/d/1Cl01HXFAPE3ZeoFknO5-DXPAm4MRE_79/view?usp=sharing), which you may have to download to view.

<p align="middle">
  <img src="https://user-images.githubusercontent.com/30236733/189579046-c9d7e0f5-11c9-425e-b6a4-8924cb486754.png" height="500" />
  <img src="https://user-images.githubusercontent.com/30236733/189579057-5f52a8ea-cd19-4f88-b871-c6b67c8e8147.png" height="500" />
  <img src="https://user-images.githubusercontent.com/30236733/189579064-90b6f5cd-7021-4b6e-ba7c-bdcb0c3c2e02.png" height="500" />
</p>
<p align="middle">
  <img src="https://user-images.githubusercontent.com/30236733/189579067-d7fa4cf2-ff15-443d-b7b3-c5efbd7f5fbf.png" height="500" />
  <img src="https://user-images.githubusercontent.com/30236733/189579913-5a7265f1-636d-41c6-9a52-abda0c6d8e71.png" height="500" /> 
</p>
<p align="middle">
  <img src="https://user-images.githubusercontent.com/30236733/189579866-ae21c102-9ee3-43b0-b6ca-da22b2a7e317.png" height="500" />
</p>


## Product Spec

HS = high school student(s)  
C = college student(s)

### User Stories

* User can sign up with a HS or C account
* User can log in/log out
* User can view their own profile
* HS can see a list of college suggestions by different categories (*CollegeAI API*)
* HS can search for college by name or with a filter (*CollegeAI API*)
    * User can see basic info about the college (*CollegeAI API*)
    * User can see C-uploaded photos organized by static albums (all, campus, dorms, food, student life)
    * User can see list of C at that college
* HS can start conversation with C from C's profile
* Users can instant message others
* Users receive notifications of conversations started and instant messages received (*Firebase Notifications*)
* C can share pinned location with HS to meet up on campus
    * C can click a location on the map (*GoogleMaps SDK*)
    * C can also search a location by name (*GoogleMaps SDK*)
* Users can open Google Maps with directions to the designated meetup location
* C can upload photos to photo albums of their college
* Users can voice call each other (*Sinch*, *Firebase Notifications*)
* HS can save colleges to their account
* HS can view their saved colleges and organize them into a category (safety, match, reach)


**Tab Navigation**

* Search (*HS*) / College info (*C*)
* Conversations
* Profile

**External Resources**
* Parse
* CollegeAI API
* GoogleMaps SDK
* Sinch
* Firebase Notifications


## Schema 

### Models
User
| Property                     | Type         | Description |
| --------                     | --------     | -------- |
| objectId                     | String       | unique user id |
| type                         | String       | "high school" or "college" |
| name                         | String       | user's name |
| profileImage                 | File         | user's profile image |
| password                     | String       | user's password |
| email                        | String       | user's email |
| from                         | String       | user's hometown |
| college                      | String       | user's current college |
| high school                  | String       | user's (current or past) high school |
| about                        | String       | user's bio |
| grade                        | String       | user's grade (freshman, sophomore, etc) |
| academicInterests            | List<String> | user's academic interests |
| extracurricularsInterests    | List<String> | user's extracurricular interests |
| collegeUnitId                | String       | user's current college's unit id in CollegeAPI |
   
Message
| Property              | Type                  | Description |
| --------              | --------              | -------- |
| objectId              | String                | unique message id |
| body                  | String                | body of the message |
| conversation          | ConversationPointer   | pointer to Conversation that the message is in |
| sender                | UserPointer           | pointer to User that sent the message |
   
Conversation
| Property              | Type                  | Description |
| --------              | --------              | -------- |
| objectId              | String                | unique conversation id |
| highSchoolStudent     | UserPointer           | pointer to high school student in conversation |
| collegeStudent        | UserPointer           | pointer to college student in conversation |
| meetLocation          | LatLng                | coordinates of a location to meet up, set by collegeStudent |
   
CollegeMedia
| Property              | Type                  | Description |
| --------              | --------              | -------- |
| objectId              | String                | unique college media id | 
| file                  | File                  | file containing uploaded media (photo/video) |
| caption               | String                | caption of the media (optional) |  
| user                  | UserPointer           | pointer to user who uploaded the media | 
| albumName             | String                | name of the album the media is in (dorms, food, etc.) |
| collegeUnitId         | String                | unit id in CollegeAPI of the college that the media was uploaded for |  
