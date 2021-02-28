package ru.skillbranch.devintensive.ui.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.item_chat_group.*
import kotlinx.android.synthetic.main.item_chat_single.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.ui.custom.AvatarImageView

class ChatAdapter(val listener: (ChatItem) -> Unit) : RecyclerView.Adapter<ChatAdapter.ChatItemViewHolder>() {

    companion object{
        private const val ARCHIVE_TYPE = 0
        private const val SINGLE_TYPE = 1
        private const val GROUP_TYPE = 2
    }

    var items: List<ChatItem> = listOf()

    override fun getItemViewType(position: Int): Int = when(items[position].chatType) {
        ChatType.ARCHIVE -> ARCHIVE_TYPE
        ChatType.SINGLE -> SINGLE_TYPE
        ChatType.GROUP -> GROUP_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val inflater = LayoutInflater.from(parent.context) //parent это recycleview
        return  when(viewType) {
            SINGLE_TYPE -> SingleViewHolder(inflater.inflate(R.layout.item_chat_single, parent, false))
            GROUP_TYPE -> GroupViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false))
            else -> SingleViewHolder(inflater.inflate(R.layout.item_chat_single, parent, false))
        }
    /*val convertView = inflater.inflate(R.layout.item_chat_single, parent, false)
        Log.d("M_ChatAdapter","onCreateViewHolder")
        return SingleViewHolder(convertView)*/
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        Log.d("M_ChatAdapter","onBindViewHolder $position")
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    //Передаются те данные которые отобразятся на экране
    fun updateData(data: List<ChatItem>) {

        Log.d("M_ChatAdapter","update data adapter - new data ${data.size} hash: ${data.hashCode()}" +
                "old data ${items.size} hash: ${items.hashCode()}")

        val diffCallback = object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean = items[oldPos].id == data[newPos].id

            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean = items[oldPos].hashCode() == data[newPos].hashCode()

            override fun getOldListSize(): Int = items.size
            override fun getNewListSize(): Int = data.size
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    abstract inner class ChatItemViewHolder(convertView: View) : RecyclerView.ViewHolder(convertView), LayoutContainer {
        override val containerView: View?
            get() = itemView

        abstract fun bind(item: ChatItem, listener: (ChatItem) -> Unit)
    }

    inner class SingleViewHolder(convertView: View) : ChatItemViewHolder(convertView), ItemTouchViewHolder{

        //Связывание модельных данных ChatItem с представлениями views
        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {

            if (item.avatar == null) {
                //Если ссылка на изобр пуста, то мы должны указать Glide прекратить загружать контент прямо сейчас
                Glide.with(itemView)
                    .clear(iv_avatar_single)
                iv_avatar_single.setInitials(item.initials)
            }else {
                //Загрузка изображения
                Glide.with(itemView) //with (контекст)
                    .load(item.avatar) //load (URI) ссылка
                    .into(iv_avatar_single) //Таргет на представление
            }

            sv_indicator.visibility = if (item.isOnline) View.VISIBLE else View.GONE
            with(tv_date_single) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }

            with(tv_counter_single) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }

            tv_title_single.text = item.title
            tv_message_single.text = item.shortDescription
            itemView.setOnClickListener{
                listener.invoke(item)
            }
        }

        //Прежде надо добавить "androidExtensions {experimental = true}" в builde.graddle(app)
/*        override val containerView: View?
            get() = itemView*/

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    inner class GroupViewHolder(convertView: View) : ChatItemViewHolder(convertView), ItemTouchViewHolder{

        //Связывание модельных данных ChatItem с представлениями views
        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {

            iv_avatar_group.setInitials(item.title[0].toString())

            //sv_indicator.visibility = if (item.isOnline) View.VISIBLE else View.GONE
            with(tv_date_group) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }

            with(tv_counter_group) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }

            tv_title_group.text = item.title
            tv_message_group.text = item.shortDescription
            with(tv_message_author) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.author
            }
            itemView.setOnClickListener{
                listener.invoke(item)
            }
        }

        //Прежде надо добавить "androidExtensions {experimental = true}" в builde.graddle(app)
/*        override val containerView: View?
            get() = itemView*/

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }
}