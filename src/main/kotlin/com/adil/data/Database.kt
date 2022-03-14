package com.adil.data

import com.adil.data.collections.*
import com.adil.security.hashPassword
import com.adil.utils.Constants.ANTE_BACKEND
import com.adil.utils.Constants.MONGODB_URI
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

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