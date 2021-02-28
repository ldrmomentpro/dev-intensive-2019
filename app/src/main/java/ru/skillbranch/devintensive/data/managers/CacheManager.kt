package ru.skillbranch.devintensive.data.managers

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.User
import ru.skillbranch.devintensive.utils.DataGenerator
//имитация базы данных
object CacheManager {
    private val chats = mutableLiveData(DataGenerator.stabChats)
    private val users = mutableLiveData(DataGenerator.stabUsers)

    fun loadChats() : MutableLiveData<List<Chat>> {
        return chats
    }

    fun findUsersByIds(ids: List<String>) : List<User> {
        return users.value!!.filter { ids.contains(it.id) }
    }

    fun nextChatId(): String{
        return "${chats.value!!.size}"
    }

    //Сохранение и добавление чата
    fun insertChat(chat: Chat) {
        val copy = chats.value!!.toMutableList()
        copy.add(chat)
        chats.value = copy
    }
}