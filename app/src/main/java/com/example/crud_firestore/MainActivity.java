package com.example.crud_firestore;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etNombre, etCedula, etEdad, etEmail;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCedula = findViewById(R.id.et_cedula);
        etNombre = findViewById(R.id.et_nombre);
        etEdad = findViewById(R.id.et_edad);
        etEmail = findViewById(R.id.et_email);

        Button btnSave = findViewById(R.id.btn_add_user);
        Button btnRead = findViewById(R.id.btn_read_user);
        Button btnUpdate = findViewById(R.id.btn_update_user);
        Button btnDelete = findViewById(R.id.btn_delete_user);

        db = FirebaseFirestore.getInstance();

        // Botón para agregar un nuevo usuario
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                String cedula = etCedula.getText().toString();
                String edadStr = etEdad.getText().toString();
                String emailStr = etEmail.getText().toString();

                if (!nombre.isEmpty() && !cedula.isEmpty() && !edadStr.isEmpty() && !emailStr.isEmpty()) {
                    int edad = Integer.parseInt(edadStr);  // Convertir la edad a int
                    saveToFirestore(nombre, cedula, edad, emailStr);
                } else {
                    Toast.makeText(MainActivity.this, "Por favor ingrese todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Botón para leer usuario por cédula
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cedula = etCedula.getText().toString();
                if (!cedula.isEmpty()) {
                    readFromFirestore(cedula);
                } else {
                    Toast.makeText(MainActivity.this, "Por favor ingrese la cédula para buscar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Botón para actualizar un usuario por cédula
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cedula = etCedula.getText().toString();
                String nombre = etNombre.getText().toString();
                String edadStr = etEdad.getText().toString();
                String emailStr = etEmail.getText().toString();

                if (!cedula.isEmpty() && !nombre.isEmpty() && !edadStr.isEmpty() && !emailStr.isEmpty()) {
                    int edad = Integer.parseInt(edadStr);  // Convertir la edad a int
                    updateUser(cedula, nombre, edad, emailStr);
                } else {
                    Toast.makeText(MainActivity.this, "Por favor ingrese todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Botón para eliminar un usuario por cédula
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cedula = etCedula.getText().toString();
                if (!cedula.isEmpty()) {
                    deleteUser(cedula);
                } else {
                    Toast.makeText(MainActivity.this, "Por favor ingrese la cédula para eliminar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Función para guardar los datos en Firestore
    private void saveToFirestore(String nombre, String cedula, int edad, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", nombre);
        user.put("cedula", cedula);
        user.put("edad", edad);
        user.put("email", email);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Función para leer los datos de un usuario
    private void readFromFirestore(String cedula) {
        db.collection("users")
                .whereEqualTo("cedula", cedula)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            etNombre.setText(document.getString("nombre"));
                            etEdad.setText(String.valueOf(document.getLong("edad")));
                            etEmail.setText(document.getString("email"));
                        } else {
                            Toast.makeText(MainActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error al leer los datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Función para actualizar los datos de un usuario
    private void updateUser(String cedula, String nombre, int edad, String email) {
        db.collection("users")
                .whereEqualTo("cedula", cedula)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String documentId = document.getId();

                            Map<String, Object> updatedData = new HashMap<>();
                            updatedData.put("nombre", nombre);
                            updatedData.put("edad", edad);
                            updatedData.put("email", email);

                            db.collection("users")
                                    .document(documentId)
                                    .update(updatedData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MainActivity.this, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Error al actualizar el usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error al buscar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Función para eliminar un usuario
    private void deleteUser(String cedula) {
        db.collection("users")
                .whereEqualTo("cedula", cedula)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String documentId = document.getId();

                            db.collection("users")
                                    .document(documentId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MainActivity.this, "Usuario eliminado exitosamente", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error al buscar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
