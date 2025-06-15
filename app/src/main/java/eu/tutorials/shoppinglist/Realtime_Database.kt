package eu.tutorials.shoppinglist

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Realtime_Database {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val dbRef = uid?.let {
        FirebaseDatabase.getInstance().getReference("shoppingList").child(it)
    }

    fun addItem(item: ShoppingListItemClass) {
        val key = dbRef?.push()?.key ?: return
        dbRef.child(key).setValue(item)
    }

    fun deleteItemById(itemId: Int) {
        dbRef?.get()?.addOnSuccessListener {
            for (child in it.children) {
                val item = child.getValue(ShoppingListItemClass::class.java)
                if (item?.id == itemId) {
                    dbRef.child(child.key!!).removeValue()
                    break
                }
            }
        }
    }

    fun deleteAllUserData(onComplete: (Boolean) -> Unit) {
        dbRef?.removeValue()
            ?.addOnSuccessListener { onComplete(true) }
            ?.addOnFailureListener { onComplete(false) }
    }

    fun updateItem(item: ShoppingListItemClass) {
        dbRef?.get()?.addOnSuccessListener {
            for (child in it.children) {
                val existingItem = child.getValue(ShoppingListItemClass::class.java)
                if (existingItem?.id == item.id) {
                    dbRef.child(child.key!!).setValue(item)
                    break
                }
            }
        }
    }

    fun getItems(onDataReceived: (List<ShoppingListItemClass>) -> Unit) {
        dbRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemList = mutableListOf<ShoppingListItemClass>()
                for (child in snapshot.children) {
                    val item = child.getValue(ShoppingListItemClass::class.java)
                    item?.let { itemList.add(it) }
                }
                onDataReceived(itemList)
            }

            override fun onCancelled(error: DatabaseError) {
                // You may log or handle errors here
            }
        })
    }
}
