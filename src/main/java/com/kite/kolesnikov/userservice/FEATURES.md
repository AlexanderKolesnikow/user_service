# Skills
1. POST /skill: Create a skill. Validate for the presence of a name and its uniqueness.
2. GET /skill/{userId}: Retrieve all of a user's skills.
3. GET /skill/{userId}/offered: Access all skills offered to a user by others. Returns skills and the number of offers.

# Recommendations
1. POST /recommendation: Create a recommendation. Validate for the presence of text. Ensure no duplicate skills and that the recommendation hasn't been given to the same user in the past 6 months.If the recipient already has the proposed skill, then the author of the recommendation is added as a guarantor to this skill, if they haven't guaranteed this skill for this user before.
2. PUT /recommendation/{id}: Update a recommendation. Validate for the presence of text and ensure no duplicate skills.
3. DELETE /recommendation/{id}: Delete a recommendation and all associated proposed skills.
4. GET /recommendation/received/{receiverId}: Access all recommendations received by a user.
5. GET /recommendation/given/{authorId}: Access all recommendations given by a user.
6. POST /recommendation/request: Request a recommendation from another user. Validate for the presence of a message, the existence of the users, and that only one request can be made every six months.
7. POST /recommendation/request/list: Get recommendation requests with various filters. Ensure the filter system is expandable.
8. GET /recommendation/request/{id}: Access a specific recommendation request by its ID.

# Subscriptions
1. A user can subscribe to another user via POST /subscription/{followerId}/user/{followeeId} endpoint. Subscription is only possible if the user hasn't already subscribed to this user before. And if they aren't subscribing to themselves.
2. A user can unsubscribe from another user via DELETE /subscription/{followerId}/user/{followeeId} endpoint. Unsubscription is only possible if they aren't unsubscribing from themselves.
3. A user can retrieve all subscribers via GET /subscription/{followeeId}/followers endpoint. They can filter them based on various expandable criteria using the UserFilterDto and UserFilter components.
4. A user can get the count of subscribers through the GET /subscription/{followeeId}/followers/count endpoint. They can filter them using various expandable criteria with the UserFilterDto and UserFilter components.
5. Users can retrieve all their subscriptions via the GET /subscription/{followerId}/followees endpoint.
6. A user can get the count of their subscriptions through the GET /subscription/{followerId}/followees/count endpoint.

# Mentorship
1. A user can send another user a mentorship request via the POST /mentorship/request endpoint. The validation is done based on the presence of users and the fact that a request can be made only once every 3 months. Users cannot send a request to themselves.
2. Users can get mentorship requests through the POST /mentorship/request/list endpoint with various filters. The filter system should be expandable for future scenarios.
3. A user can accept a mentorship request via POST /mentorship/request/{id}/accept. Here, the request status is updated to ACCEPTED, and the users are added to each other's list of mentors/mentees.
4. A user can decline a mentorship request, providing a reason, via POST /mentorship/request/{id}/reject. Here, the request status is updated to REJECTED, and the reason is set.

# Events
1. A user can create an event via POST /event endpoint. Validation is done based on the presence of a title, and it's also checked that the user possesses the Skills for which they are creating the event.
2. A user can retrieve a specific event by its id via the GET /event/{id} endpoint.
3. A user can retrieve events with filters via the GET /event/list endpoint. The filter system should be expandable for future scenarios.
4. A user can delete an event by its id via the DELETE endpoint.
5. A user can update the information about an event via the /event/{id} endpoint, and it's also checked that the user possesses the Skills for which they are creating the event.
6. A user can retrieve all events they've created via the GET /event/{userId}/owned endpoint.
7. A user can retrieve all events they're participating in via the GET /event/{userId}/participated endpoint.

# EventParticipation
1. A user can register for an event via the POST /{eventId}/register/{userId} endpoint. The user cannot register multiple times.
2. A user can unregister from an event via the POST /{eventId}/unregister/{userId} endpoint. The user must be registered.
3. A user can get the list of all participants registered for an event via the GET /{eventId}/participants endpoint.
4. A user can get the count of all participants registered for an event via the GET /{eventId}/participants/count endpoint.

# ProfilePicture
1. POST /users/{userId}/profilePic: User can upload their avatar.
2. GET /users/{userId}/profilePic: User can retrieve their avatar in 1080x1080 resolution (currently only in jpg format).
3. GET /users/{userId}/profilePicSmall: User can retrieve their avatar in 170x170 resolution (currently only in jpg format).
4. DELETE /users/{userId}/profilePic: User can delete their avatar.

# Goals
1. POST /users/{userId}/goals: User can retrieve all of a user's goals with filters.
2. POST {userId}/goal/create: User can create a new goal. Validate for the presence of a name. A user can only have 3 active goals at the same time, also check that existing skills are provided.
3. POST /goals/{goalId}: User can update an existing goal. Validate for the presence of a name. Can't complete an already completed goal. If the goal is moved from active to completed, the skills associated with the goal, if they were in the goal, are assigned to the user. When updating the goal, a separate repository method is called to update the skills (update = delete + add).
4. DELETE /goals/{goalId}: User can delete a goal.
5. POST /goals/{goalId}/subtasks: User can retrieve all subtasks of a goal with filters.

# GoalInvitation
1. POST /goal/invitation: User can send an invitation to join a goal to another user. Validate for the presence of the sender and the invited user in the database. Can't send an invitation to oneself.
2. PUT /goal/invitation/{id}/accept: User can accept an invitation to join a goal. Check that the user is not already pursuing this goal. A user can accept an invitation only if they don't have too many active goals. Currently, this is 3.
3. PUT /goal/invitation/{id}/reject: User can reject an invitation to join a goal. Check that the user is not already pursuing this goal.
4. POST /goal/invitation/list: User can view a list of all invitations with filters.
