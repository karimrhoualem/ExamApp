package edu.coen390.androidapp.Controller;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.EnhancedUser;
import edu.coen390.androidapp.Model.Invigilator;
import edu.coen390.androidapp.Model.Professor;
import edu.coen390.androidapp.Model.Student;
import edu.coen390.androidapp.Model.User;

public class FireStoreDBHelper {
    private final String TAG = "FireStoreDBHelper";
    private final FirebaseFirestore db;

    public FireStoreDBHelper(FirebaseFirestore db) {
        this.db = db;
    }

    public FireStoreDBHelper(){
        db = FirebaseFirestore.getInstance();
    }

    public void getData(){
        ArrayList<CollectionReference> collectionReferenceArrayList = new ArrayList<>();
        Map<String, Map<String, QueryDocumentSnapshot>> documentContentsList = new Hashtable<>();
        String [] collections = new String[]
                {
                        "courses",
                        "gmail",
                        "invigilators",
                        "professors",
                        "students"
                };

        ArrayList<EnhancedUser> usersList = new ArrayList<>();
        ArrayList<Course> courseArrayList = new ArrayList<>();
        ArrayList<String> gmailArrayList = new ArrayList<>();

        for(String collection:collections)
            collectionReferenceArrayList.add(db.collection(collection));

        for(CollectionReference collectionReference : collectionReferenceArrayList){
            collectionReference
                    .get()
                    .addOnCompleteListener(task -> {
                       if(task.isSuccessful()){
                           String collectionKey = collectionReference.getId();
                           Map<String, QueryDocumentSnapshot> documentsMap = new Hashtable<>();
                           for(QueryDocumentSnapshot document : task.getResult()){
                              documentsMap.put(document.getId(), document);

                           }
                           documentContentsList.put(collectionKey, documentsMap);

                           for(Map.Entry<String, Map<String, QueryDocumentSnapshot>> entry : documentContentsList.entrySet()){
                               for(Map.Entry<String, QueryDocumentSnapshot> documentSnapshotEntry : entry.getValue().entrySet()){
                                   switch (entry.getKey()) {
                                       case "professors":
                                       case "invigilators":
                                           usersList.add(documentSnapshotEntry.getValue().toObject(EnhancedUser.class));
                                           break;
                                       case "courses":
                                           //courseArrayList.add(documentSnapshotEntry.getValue().toObject(Course.class));
                                           break;
                                       default:
                                           break;
                                   }
                               }
                           }
                           for(EnhancedUser user: usersList) {
                               Log.d(TAG, user.toString());
                           }

                       }else{
                           Log.d(TAG, "Error getting documents: ", task.getException());
                       }
                    });
        }
    }

    void readData(FireStoreCallback fireStoreCallback){

    }

    /*public void getData(){
        CollectionReference collectionReference = db
                .collection("data");

        collectionReference
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Map<String, DocumentReference> map = new Hashtable<>();
                        ArrayList<User> usersList = new ArrayList<>();
                        ArrayList<Course> courseArrayList = new ArrayList<>();
                        ArrayList<String> gmailArrayList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DocumentReference documentReference;

                            switch (document.getId()) {
                                case "professors":
                                    documentReference = db
                                            .collection("data")
                                            .document("professors");
                                    map.put(document.getId(), documentReference);
                                    break;
                                case "students":
                                    documentReference = db
                                            .collection("data")
                                            .document("students");
                                    map.put(document.getId(), documentReference);
                                    break;
                                case "invigilators":
                                    documentReference = db
                                            .collection("data")
                                            .document("invigilators");
                                    map.put(document.getId(), documentReference);
                                    break;
                                case "courses":
                                    documentReference = db
                                            .collection("data")
                                            .document("courses");
                                    map.put(document.getId(), documentReference);
                                    break;
                                default:
                                    break;
                            }

                        }

                        for (Map.Entry<String, DocumentReference> entry : map.entrySet()) {
                            entry.getValue().get().addOnSuccessListener(documentSnapshot -> {
                                switch (entry.getKey()) {
                                    case "professors":
                                        usersList.add(documentSnapshot.toObject(Professor.class));
                                        break;
                                    case "students":
                                        usersList.add(documentSnapshot.toObject(Student.class));
                                        break;
                                    case "invigilators":
                                        usersList.add(documentSnapshot.toObject(Invigilator.class));
                                        break;
                                    case "courses":
                                        courseArrayList.add(documentSnapshot.toObject(Course.class));
                                        break;
                                    default:
                                        break;
                                }
                            });
                        }



                    }else{
                        Log.d(TAG, "Data Collection not retrieved");
                    }
        });
    }*/

    /*
      //Log.d(TAG, document.getId() + " => " + document.getData());
                            map.put(document.getId(), document.getData());
                            Log.d(TAG, map.toString());

                            switch (document.getId()){
                                case "professors":
                                    usersList.add(document.toObject(Professor.class));
                                    break;
                                case "students":
                                    usersList.add(document.toObject(Student.class));
                                    break;
                                case "invigilators":
                                    usersList.add(document.toObject(Invigilator.class));
                                    break;
                                case "courses":
                                    courseArrayList.add(document.toObject(Course.class));
                                    break;
                                default:
                                    break;
                            }
                        }
                        for(User user: usersList){
                            Log.d(TAG, user.toString());
     */

}
