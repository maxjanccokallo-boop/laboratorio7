package com.nexarion.datossinmvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// El ViewModel necesita el DAO para funcionar
class UserViewModel(private val dao: UserDao) : ViewModel() {

    // --- READ (Leer) ---
    // Tomamos el 'Flow' del DAO y lo convertimos en un 'StateFlow'
    // que la UI pueda observar.
    // La lista se actualizará sola.
    val allUsers: StateFlow<List<User>> = dao.getAll()
        .stateIn(
            scope = viewModelScope, // El 'scope' del ViewModel
            started = SharingStarted.WhileSubscribed(5000L), // Inicia cuando la UI observa
            initialValue = emptyList() // Valor inicial (lista vacía)
        )

    // --- CREATE (Crear) ---
    fun addUser(firstName: String, lastName: String) {
        // Lanzamos una corutina en el scope del ViewModel
        viewModelScope.launch {
            val user = User(firstName = firstName, lastName = lastName)
            dao.insert(user)
        }
    }

    // --- DELETE (Borrar) ---
    fun deleteUser(user: User) {
        viewModelScope.launch {
            dao.delete(user)
        }
    }

    fun deleteLastUser() {
        viewModelScope.launch {
            dao.deleteLastUser()
        }
    }
}

// --- FÁBRICA (Factory) ---
// Como nuestro ViewModel necesita un 'dao' en su constructor,
// necesitamos una "Fábrica" que sepa cómo crearlo.
class UserViewModelFactory(private val dao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}