package com.adil.data

import com.adil.data.collections.*
import com.adil.security.hashPassword
import com.adil.utils.Constants.ANTE_BACKEND
import com.adil.utils.Constants.MONGODB_URI
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.match
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.regex
import org.litote.kmongo.setValue

private val client = KMongo.createClient(System.getenv(MONGODB_URI)).coroutine
private val database = client.getDatabase(ANTE_BACKEND)
private val users = database.getCollection<User>()
private val habits = database.getCollection<Habit>()
private val goals = database.getCollection<Goal>()
private val subGoals = database.getCollection<SubGoal>()
private val posts = database.getCollection<Post>()
private val comments = database.getCollection<Comment>()
//User
suspend fun registerUser(user: User): Boolean {
    return users.insertOne(user).wasAcknowledged()
}

suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    val actualPassword = findUserByEmail(email)?.password ?: return false
    return hashPassword(passwordToCheck) == actualPassword
}

suspend fun findUser(id: String): User? {
    return users.findOne(User::id eq id)
}

suspend fun findUserByEmail(email: String): User? {
    return users.findOne(User::email eq email)
}

suspend fun deleteUser(id: String): Boolean {
    val user = findUser(id)
    return user?.let { curUser ->
        users.deleteOneById(curUser.id).wasAcknowledged()
    } ?: false
}

suspend fun updateUserUsername(id: String, username: String): Boolean {
    return users.updateOne(User::id eq id, setValue(User::username, username)).wasAcknowledged()
}

suspend fun updateUserFirstName(id: String, firstName: String): Boolean {
    return users.updateOne(User::id eq id, setValue(User::firstName, firstName)).wasAcknowledged()
}

suspend fun updateUserLastName(id: String, secondName: String): Boolean {
    return users.updateOne(User::id eq id, setValue(User::lastName, secondName)).wasAcknowledged()
}

suspend fun updateUserBio(id: String, bio: String): Boolean {
    return users.updateOne(User::id eq id, setValue(User::bio, bio)).wasAcknowledged()
}

suspend fun updateUserDateOfBirth(id: String, dateOfBirth: Long): Boolean {
    return users.updateOne(User::id eq id, setValue(User::dateOfBirth, dateOfBirth)).wasAcknowledged()
}

suspend fun updateUserBackground(id: String, background: String): Boolean {
    return users.updateOne(User::id eq id, setValue(User::backgroundUrl, background)).wasAcknowledged()
}

suspend fun updateUserProfileImage(id: String, profileImage: String): Boolean {
    return users.updateOne(User::id eq id, setValue(User::profileImageUrl, profileImage)).wasAcknowledged()
}

suspend fun searchUser(query: String): List<User> {
    val result = mutableListOf<User>()
    val usernameLists = users.find(User::username regex ".*$query.*").toList()
    val firstName = users.find(User::firstName regex ".*$query.*").toList()
    val lastName = users.find(User::lastName regex ".*$query.*").toList()
    result.addAll(usernameLists)
    result.addAll(firstName)
    result.addAll(lastName)
    return result
}

suspend fun checkIfAlreadyFollowed(myId: String, userId: String): Boolean {
    val followingList = findUser(myId)?.following ?: return false
    val followersList = findUser(userId)?.followers ?: return false
    return if (followingList.contains(userId))
        true
    else followersList.contains(myId)
}

suspend fun followUser(myId: String, userId: String): Boolean {
    val followingList = findUser(myId)?.following ?: return false
    val followersList = findUser(userId)?.followers ?: return false
    val followingAction = users.updateOneById(myId, setValue(User::following, followingList + userId)).wasAcknowledged()
    val followersAction = users.updateOneById(userId, setValue(User::followers, followersList + myId)).wasAcknowledged()
    return followingAction && followersAction
}

suspend fun unfollowUser(myId: String, userId: String): Boolean {
    val followingList = findUser(myId)?.following?.toMutableList() ?: return false
    val followersList = findUser(userId)?.followers?.toMutableList() ?: return false
    followingList.remove(userId)
    followersList.remove(myId)
    val followingAction = users.updateOneById(myId, setValue(User::following, followingList)).wasAcknowledged()
    val followersAction = users.updateOneById(userId, setValue(User::followers, followersList)).wasAcknowledged()
    return followingAction && followersAction
}

//Habits
suspend fun getHabitsForUser(userId: String): List<Habit> {
    return habits.find(Habit::ownerId eq userId).toList()
}

suspend fun getHabitById(id: String): Habit? {
    return habits.findOneById(id)
}

suspend fun addHabit(habit: Habit): Boolean {
    return habits.insertOne(habit).wasAcknowledged()
}

suspend fun updateHabit(habit: Habit): Boolean {
    return habits.updateOne(Habit::id eq habit.id, habit).wasAcknowledged()
}

suspend fun markAsDoneHabit(habit: Habit, canMarkToday: Boolean): Boolean {
    return if (canMarkToday){
        habits.updateOneById(habit.id, setValue(Habit::curNumOfDays, habit.curNumOfDays + 1)).wasAcknowledged()
        habits.updateOneById(habit.id, setValue(Habit::lastTimeMarked, System.currentTimeMillis())).wasAcknowledged()
    }
    else {
        habits.updateOneById(habit.id, setValue(Habit::curNumOfDays, habit.curNumOfDays - 1)).wasAcknowledged()
        habits.updateOneById(habit.id, setValue(Habit::lastTimeMarked,  0)).wasAcknowledged()
    }
}

suspend fun deleteHabit(habitId: String): Boolean {
    return habits.deleteOneById(habitId).wasAcknowledged()
}
//Goals
suspend fun getGoalsForUser(userId: String): List<Goal> {
    return goals.find(Goal::ownerId eq userId).toList()
}

suspend fun getGoalById(id: String): Goal? {
    return goals.findOneById(id)
}

suspend fun updateGoal(goal: Goal): Boolean {
    return goals.updateOne(Goal::id eq goal.id, goal).wasAcknowledged()
}

suspend fun addGoal(goal: Goal): Boolean {
    return goals.insertOne(goal).wasAcknowledged()
}

suspend fun deleteGoal(goalId: String): Boolean {
    return goals.deleteOneById(goalId).wasAcknowledged()
}

//SubGoals
suspend fun getSubGoalForGoal(goalId: String): List<SubGoal> {
    return subGoals.find(SubGoal::goalId eq goalId).toList()
}

suspend fun addSubGoals(subGoalList: List<SubGoal>): Boolean {
    return subGoals.insertMany(subGoalList).wasAcknowledged()
}

suspend fun deleteSubGoalsForGoal(goalId: String): Boolean {
    return subGoals.deleteMany(SubGoal::goalId eq goalId).wasAcknowledged()
}

suspend fun completeSubGoal(subGoalId: String): Boolean {
    return subGoals.updateOneById(subGoalId, setValue(SubGoal::isCompleted, true)).wasAcknowledged()
}

//Posts
suspend fun getPostForUser(id: String): List<Post>{
    return posts.find(Post::ownerId eq id).toList()
}

suspend fun likeUnlikePost(id: String, userId: String): Boolean {
    val peopleLiked = posts.findOneById(id)?.peopleLiked ?: return false
    return if (peopleLiked.contains(userId)){
        posts.updateOneById(id, setValue(Post::peopleLiked, peopleLiked - userId)).wasAcknowledged()
    }else posts.updateOneById(id, setValue(Post::peopleLiked, peopleLiked + userId)).wasAcknowledged()
}

suspend fun addRegularPost(description: String, imageUrl: String?, ownerId: String): Boolean {
    val post = Post(
        dateOfCreation = System.currentTimeMillis(),
        description = description,
        imageUrl = imageUrl,
        ownerId = ownerId,
        type = PostType.REGULAR
    )
    return posts.insertOne(post).wasAcknowledged()
}

suspend fun addAchievementPost(type: PostType, achievementId: String, ownerId: String): Boolean {
    val post = Post(
        dateOfCreation = System.currentTimeMillis(),
        description = "",
        ownerId = ownerId,
        achievementId = achievementId,
        type = type
    )
    return posts.insertOne(post).wasAcknowledged()
}

//Comments
suspend fun getCommentsForPost(postId: String): List<Comment> {
    return comments.find(Comment::postId eq postId).toList()
}

suspend fun addComment(postId: String, text: String, authorId: String): Boolean {
    val comment = Comment(
        dateOfCreation = System.currentTimeMillis(),
        text = text,
        postId = postId,
        peopleHelpful = emptyList(),
        peopleLiked = emptyList(),
        authorId = authorId
    )
    return comments.insertOne(comment).wasAcknowledged()
}

suspend fun likeUnlikeComment(id: String, userId: String): Boolean {
    val peopleLiked = comments.findOneById(id)?.peopleLiked ?: return false
    return if (peopleLiked.contains(userId)){
        comments.updateOneById(id, setValue(Comment::peopleLiked, peopleLiked - userId)).wasAcknowledged()
    }else comments.updateOneById(id, setValue(Comment::peopleLiked, peopleLiked + userId)).wasAcknowledged()
}

suspend fun helpfulComment(id: String, userId: String): Boolean {
    val peopleHelpful = comments.findOneById(id)?.peopleHelpful ?: return false
    return if (peopleHelpful.contains(userId)){
        comments.updateOneById(id, setValue(Comment::peopleHelpful, peopleHelpful - userId)).wasAcknowledged()
    }else comments.updateOneById(id, setValue(Comment::peopleHelpful, peopleHelpful + userId)).wasAcknowledged()
}