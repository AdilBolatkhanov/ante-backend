package com.adil.data

import com.adil.data.collections.*
import com.adil.security.hashPassword
import com.adil.utils.Constants.ANTE_BACKEND
import com.adil.utils.Constants.MONGODB_URI
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

private val client = KMongo.createClient(System.getenv(MONGODB_URI)).coroutine
private val database = client.getDatabase(ANTE_BACKEND)
private val users = database.getCollection<User>()
private val habits = database.getCollection<Habit>()
private val goals = database.getCollection<Goal>()
private val subGoals = database.getCollection<SubGoal>()
private val posts = database.getCollection<Post>()
private val comments = database.getCollection<Comment>()
private val images = database.getCollection<Image>()
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

//Habits
suspend fun getHabitsForUser(userId: String): List<Habit> {
    return habits.find(Habit::ownerId eq userId).toList()
}

//Goals
suspend fun getGoalsForUser(userId: String): List<Goal> {
    return goals.find(Goal::ownerId eq userId).toList()
}

//SubGoals

suspend fun getSubGoalForGoal(goalId: String): List<SubGoal> {
    return subGoals.find(SubGoal::goalId eq goalId).toList()
}

//Posts
suspend fun getPostForUser(goalId: String): List<Post>{
    return posts.find(Post::ownerId eq goalId).toList()
}

//Comments
suspend fun getCommentsForPost(postId: String): List<Comment> {
    return comments.find(Comment::postId eq postId).toList()
}

//Image
suspend fun addImage(image: Image): Boolean {
    return images.insertOne(image).wasAcknowledged()
}

suspend fun getImage(id: String): Image? {
    return images.findOne(Image::id eq id)
}