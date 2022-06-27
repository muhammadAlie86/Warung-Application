package com.example.warungapplication.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.warungapplication.data.model.User
import com.example.warungapplication.data.model.Warung
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WarungRepository@Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase

) : IWarungRepository {
    private val warungId = "user"
    private val warungMutableLiveData : MutableLiveData<Warung> = MutableLiveData()
    private var userMutableLiveData : MutableLiveData<FirebaseUser> = MutableLiveData()
    private var loggedMutableLiveData : MutableLiveData<Boolean> = MutableLiveData()

    override fun addUser(user: User) {

        auth.createUserWithEmailAndPassword(user.email,user.password).addOnCompleteListener { task->
            if (task.isSuccessful && auth.currentUser != null ){
                userMutableLiveData.postValue(auth.currentUser)
            }
            else{
                Log.d("Repository",task.exception.toString())
            }
        }
    }


    override fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
            if (task.isSuccessful){
                userMutableLiveData.postValue(auth.currentUser)
            }
            else{
                Log.d("Repository",task.exception.toString())
            }
        }
    }

    override fun signOut() {
        auth.signOut()
        loggedMutableLiveData.postValue(true)
    }

    override fun addWarung(warung: Warung) {

        val path = firebaseDatabase.getReference("warung")

        path.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                path.child(warungId).child("name").setValue(warung.name)
                path.child(warungId).child("address").setValue(warung.address)
                path.child(warungId).child("location").setValue(warung.location)
                path.child(warungId).child("image").setValue(warung.imgWarung)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Warung Repository", error.message )
            }

        })
    }

    override fun getAllWarung() : MutableLiveData<List<Warung>>{

        val path = firebaseDatabase.getReference("warung")
        val warungMutableLiveData : MutableLiveData<List<Warung>> = MutableLiveData()

        val listWarung = ArrayList<Warung>()
        path.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listWarung.clear()
                    for (data in snapshot.children) {

                        val name = data.child("name").value.toString()
                        val address = data.child("address").value.toString()
                        val imgWarung = data.child("image").value.toString()
                        val location = data.child("location").value.toString()

                        val warung = Warung(
                            name = name,
                            address = address,
                            imgWarung = imgWarung,
                            location = location
                        )
                        listWarung.addAll(listOf(warung))

                    }
                    warungMutableLiveData.postValue(listWarung)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return warungMutableLiveData
    }

    override fun editWarung(warung: Warung) {

        val path = firebaseDatabase.getReference("warung").child(warungId)

        val hashMap = HashMap<String,Any>()
        hashMap["name"] = warung.name
        hashMap["address"] = warung.address
        hashMap["location"] = warung.location
        hashMap["image"] = warung.imgWarung

        path.updateChildren(hashMap).addOnSuccessListener {
            warungMutableLiveData.postValue(warung)
        }.addOnFailureListener {
            Log.d("Warung Repo", it.message.toString())
        }

    }

    override fun deleteWarung(warung: Warung) {
        val path = firebaseDatabase.getReference("warung")
        path.child("user").removeValue().addOnSuccessListener {
            warungMutableLiveData.postValue(warung)
        }.addOnFailureListener {
            Log.d("Warung Repo", it.message.toString())
        }
    }
}