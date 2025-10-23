package com.nexarion.datossinmvvm

// --- IMPORTACIONES ESPECÍFICAS ---
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importa el ViewModel y la nueva forma de recolectar 'State'
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser() {

    // --- 1. OBTENER EL VIEWMODEL ---
    val context = LocalContext.current
    // Obtenemos el DAO desde nuestra 'Application'
    val dao = (context.applicationContext as MyApplication).database.userDao()
    // Creamos el ViewModel usando nuestra Fábrica
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(dao))

    // --- 2. OBTENER LOS DATOS (REACTIVOS) ---
    // Observamos el 'StateFlow' del ViewModel.
    // 'users' se actualizará automáticamente.
    val users by viewModel.allUsers.collectAsStateWithLifecycle()

    // Estados locales para los campos de texto
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Room con ViewModel") },
                actions = {
                    // --- BOTÓN "AGREGAR" ---
                    IconButton(onClick = {
                        if (firstName.isNotBlank() && lastName.isNotBlank()) {
                            viewModel.addUser(firstName, lastName)
                            // Limpiamos los campos
                            firstName = ""
                            lastName = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, "Agregar Usuario")
                    }

                    // --- BOTÓN "BORRAR ÚLTIMO" ---
                    // (Reemplazamos 'Listar', ya no es necesario)
                    IconButton(onClick = {
                        viewModel.deleteLastUser()
                    }) {
                        Icon(Icons.Default.Delete, "Borrar Último")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // --- Formulario de Entrada ---
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Divider()

            // --- 3. LISTA REACTIVA ---
            // Ya no usamos un 'Text' simple, usamos una lista.
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 'users' viene del 'collectAsStateWithLifecycle'
                items(users) { user ->
                    UserItem(
                        user = user,
                        onDeleteClick = {
                            viewModel.deleteUser(user) // <-- CRUD Completo
                        }
                    )
                }
            }
        }
    }
}

// Un Composable bonito para cada item de la lista
@Composable
fun UserItem(user: User, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(text = "ID: ${user.uid}", fontSize = 12.sp)
            }
            // Botón para borrar este usuario específico
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Borrar Usuario",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onDeleteClick() } // Evento click
            )
        }
    }
}

// --- ¡YA NO NECESITAS LAS FUNCIONES 'crearDatabase', 'getUsers', 'AgregarUsuario', etc., AQUÍ! ---
// --- TODO ESTÁ EN EL VIEWMODEL. ¡MUCHO MÁS LIMPIO! ---