package com.ab.anti_spam.firebase

import androidx.lifecycle.MutableLiveData
import com.ab.anti_spam.helpers.SHA256
import com.ab.anti_spam.models.*
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


object FirebaseDBManager : CommunityDBInterface, FirestoreDBInterface {
    override fun createCommunityReport(model: CommunityBlockingModel, currentUserUID: String) {
       val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("community-reports").child(currentUserUID)
        database.child(model.id.toString()).setValue(model)
    }

    override fun deleteCommunityReport(model: CommunityBlockingModel, currentUserUID: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("community-reports").child(currentUserUID)
        database.child(model.id.toString()).removeValue().addOnSuccessListener {
            println("Successfully removed report: +${model.id}")
        }.addOnFailureListener{
            println(it.message)
        }
    }

    override fun deleteComment(model: CommunityBlockingCommentsModel,reportUID:String,reportId:String) {

        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("community-reports")
        database.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                val localModel = ArrayList<CommunityBlockingModel>()
                for(data:DataSnapshot in data.children){

                    for(data in data.children) {
                        if(data.child("id").getValue().toString().equals(reportId)) {

                            val id = (data.child("id").getValue().toString().toLong())
                            val user_id = (data.child("user_Id").getValue().toString())
                            val report_Description =
                                (data.child("report_Description").getValue().toString())
                            val reported_phone_number =
                                (data.child("reported_phone_number").getValue().toString())
                            val risk_level = (data.child("risk_Level").getValue().toString())
                            val country = (data.child("country").getValue().toString())
                            val date = (data.child("date").getValue().toString())
                            val user_comments = data.child("user_comments").getValue(object : GenericTypeIndicator<MutableList<CommunityBlockingCommentsModel>>() {})
                            var comments = mutableListOf<CommunityBlockingCommentsModel>()
                            if (user_comments != null) {
                                comments = user_comments
                            }
                            val model = CommunityBlockingModel(id, user_id, report_Description, reported_phone_number, risk_level, country,date, comments)

                            localModel.add(model)
                        }
                    }
                }
                localModel[0].user_comments.remove(model)

                val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("community-reports").child(reportUID)
                database.child(localModel[0].id.toString()).updateChildren(localModel[0].map()).addOnSuccessListener {
                    println("Successfully updated report: ${localModel[0].id}")
                }.addOnFailureListener{
                    println(it.message)
                }

            }
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    override fun updateCommunityReport(model: CommunityBlockingModel, currentUserUID: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("community-reports").child(currentUserUID)
        database.child(model.id.toString()).updateChildren(model.map()).addOnSuccessListener {
            println("Successfully updated report: ${model.id}")
        }.addOnFailureListener{
            println(it.message)
        }
    }

    override fun getTop100CommunityReports(model: MutableLiveData<MutableList<CommunityBlockingModel>>) {
        val localModel = ArrayList<CommunityBlockingModel>()
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("community-reports")
        database.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                for(data:DataSnapshot in data.children){
                    if(localModel.size <= 100) {
                        for(data in data.children) {

                            val id = (data.child("id").getValue().toString().toLong())
                            val user_id = (data.child("user_Id").getValue().toString())
                            val report_Description =
                                (data.child("report_Description").getValue().toString())
                            val reported_phone_number = (data.child("reported_phone_number").getValue().toString())
                            val risk_level = (data.child("risk_Level").getValue().toString())
                            val country = (data.child("country").getValue().toString())
                            val date = (data.child("date").getValue().toString())
                            val user_comments = data.child("user_comments").getValue(object : GenericTypeIndicator<MutableList<CommunityBlockingCommentsModel>>() {})

                            var comments = mutableListOf<CommunityBlockingCommentsModel>()
                            if (user_comments != null) {
                                comments = user_comments
                            }
                            val model = CommunityBlockingModel(id, user_id, report_Description, reported_phone_number, risk_level, country,date, comments)

                            localModel.add(model)
                        }
                    }
                }
                model.value = localModel
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun getCommunityReportById(model: MutableLiveData<CommunityBlockingModel?>, reportId: String) {

        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("community-reports")
        database.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                val localModel = ArrayList<CommunityBlockingModel>()
                for(data:DataSnapshot in data.children){

                    for(data in data.children) {
                        if(data.child("id").getValue().toString().equals(reportId)) {

                            val id = (data.child("id").getValue().toString().toLong())
                            val user_id = (data.child("user_Id").getValue().toString())
                            val report_Description =
                                (data.child("report_Description").getValue().toString())
                            val reported_phone_number =
                                (data.child("reported_phone_number").getValue().toString())
                            val risk_level = (data.child("risk_Level").getValue().toString())
                            val country = (data.child("country").getValue().toString())
                            val date = (data.child("date").getValue().toString())
                            val user_comments = data.child("user_comments").getValue(object : GenericTypeIndicator<MutableList<CommunityBlockingCommentsModel>>() {})
                            var comments = mutableListOf<CommunityBlockingCommentsModel>()
                            if (user_comments != null) {
                                comments = user_comments
                            }
                            val model = CommunityBlockingModel(id, user_id, report_Description, reported_phone_number, risk_level, country,date, comments)

                            localModel.add(model)
                        }
                    }
                }
                model.value = localModel[0]
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun getCommunityReportByNumber(phoneNumber: String,callback: (CommunityBlockingModel?) -> Unit) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("community-reports")

        database.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                val localModel = ArrayList<CommunityBlockingModel>()
                for(data:DataSnapshot in data.children){
                    for(data in data.children) {
                        if(data.child("reported_phone_number").getValue().toString().equals(phoneNumber)) {

                            val id = (data.child("id").getValue().toString().toLong())
                            val user_id = (data.child("user_Id").getValue().toString())
                            val report_Description =
                                (data.child("report_Description").getValue().toString())
                            val reported_phone_number =
                                (data.child("reported_phone_number").getValue().toString())
                            val risk_level = (data.child("risk_Level").getValue().toString())
                            val country = (data.child("country").getValue().toString())
                            val date = (data.child("date").getValue().toString())
                            val user_comments = data.child("user_comments").getValue(object : GenericTypeIndicator<MutableList<CommunityBlockingCommentsModel>>() {})
                            var comments = mutableListOf<CommunityBlockingCommentsModel>()
                            if (user_comments != null) {
                                comments = user_comments
                            }
                            val foundModel = CommunityBlockingModel(id, user_id, report_Description, reported_phone_number, risk_level, country,date ,comments)

                            localModel.add(foundModel)
                        }
                    }
                }
                if(localModel.size == 1){
                    callback(localModel[0])
                }else{
                    callback(null)
                }

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun checkNumber(phoneNumber: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val blocklistRef = db.collection("blocklist")
        val query: Query = blocklistRef.whereEqualTo(FieldPath.documentId(), phoneNumber)

        query.get().addOnSuccessListener { querySnapshot ->
                val isBlocked = !querySnapshot.isEmpty
                callback(isBlocked)
            }
            .addOnFailureListener { exception ->
                // handle exceptions here
                println(exception)
                callback(false)
            }
    }

    override fun getDocumentByNumber(phoneNumber: String, callback: (LocalBlockModel?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val blocklistRef = db.collection("blocklist")
        val query: Query = blocklistRef.whereEqualTo(FieldPath.documentId(), phoneNumber)


        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]

                val number = documentSnapshot.id

                val country = documentSnapshot.getString("country")!!
                val risk = documentSnapshot.getString("risk")!!
                val user_reports = documentSnapshot.getString("user_reports")!!.split(" ")[0]


                val localBlockModel = LocalBlockModel(number,risk,country,user_reports)

                callback(localBlockModel)
            } else {
                callback(null)
            }
        }
    }


    override fun getBlockList(callback: (blocklist : MutableList<LocalBlockModel>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val blocklistRef = db.collection("blocklist")

        blocklistRef.get().addOnSuccessListener { documents ->
            val blocklist = mutableListOf<LocalBlockModel>()

            for (document in documents) {

                var phoneNumber = document.id
                val data = document.data
                val country = data["country"] as String
                val risk = data["risk"] as String
                val userReports = data["user_reports"] as String

                //Convert number to SHA256
                phoneNumber = SHA256.sha256(phoneNumber)

                val localBlock = LocalBlockModel(number = phoneNumber, risk = risk, user_comments = userReports, country = country)
                blocklist.add(localBlock)
            }
            callback(blocklist)
        }.addOnFailureListener{exception ->
            println(exception)
        }

    }


    override fun updateCommunityReportComments(model: CommunityBlockingCommentsModel, reportId: String, currentUserUID: String,reportUID: String,updateModel: MutableLiveData<CommunityBlockingModel?>) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("community-reports")
        database.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                val localModel = ArrayList<CommunityBlockingModel>()
                for(data:DataSnapshot in data.children){

                    for(data in data.children) {
                        if(data.child("id").getValue().toString().equals(reportId)) {

                            val id = (data.child("id").getValue().toString().toLong())
                            val user_id = (data.child("user_Id").getValue().toString())
                            val report_Description =
                                (data.child("report_Description").getValue().toString())
                            val reported_phone_number =
                                (data.child("reported_phone_number").getValue().toString())
                            val risk_level = (data.child("risk_Level").getValue().toString())
                            val country = (data.child("country").getValue().toString())
                            val date = (data.child("date").getValue().toString())
                            val user_comments = data.child("user_comments").getValue(object : GenericTypeIndicator<MutableList<CommunityBlockingCommentsModel>>() {})
                            var comments = mutableListOf<CommunityBlockingCommentsModel>()
                            if (user_comments != null) {
                                comments = user_comments
                            }
                            val model = CommunityBlockingModel(id, user_id, report_Description, reported_phone_number, risk_level, country,date, comments)

                            localModel.add(model)
                        }
                    }
                }

                 localModel[0].user_comments.add(model)


                val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("community-reports").child(reportUID)
                database.child(localModel[0].id.toString()).updateChildren(localModel[0].map()).addOnSuccessListener {
                    println("Successfully updated report: ${localModel[0].id}")
                    updateModel.value = localModel[0]
                }.addOnFailureListener{
                    println(it.message)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }




    override fun getPersonalReports(model: MutableLiveData<MutableList<CommunityBlockingModel>>, currentUserUID: String) {
        val localModel = ArrayList<CommunityBlockingModel>()
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("community-reports")
        database.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {

                for(data:DataSnapshot in data.children){

                    for(data in data.children) {
                    if(data.child("user_Id").getValue().toString().equals(currentUserUID)) {
                        val id = (data.child("id").getValue().toString().toLong())
                        val user_id = (data.child("user_Id").getValue().toString())
                        val report_Description =
                            (data.child("report_Description").getValue().toString())
                        val reported_phone_number =
                            (data.child("reported_phone_number").getValue().toString())
                        val risk_level = (data.child("risk_Level").getValue().toString())
                        val country = (data.child("country").getValue().toString())
                        val date = (data.child("date").getValue().toString())
                        val user_comments = data.child("user_comments").getValue(object : GenericTypeIndicator<MutableList<CommunityBlockingCommentsModel>>() {})
                        var comments = mutableListOf<CommunityBlockingCommentsModel>()
                        if (user_comments != null) {
                            comments = user_comments
                        }
                        val model = CommunityBlockingModel(id, user_id, report_Description, reported_phone_number, risk_level, country, date,comments)

                        localModel.add(model)
                    }
                    }
                }

                model.value = localModel
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }




}