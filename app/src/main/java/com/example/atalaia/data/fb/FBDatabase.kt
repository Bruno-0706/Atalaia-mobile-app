package com.example.atalaia.data.fb

import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.data.fb.FBOcorrencia
/*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
 */

class FBDatabase {

    //interface de listener (paradigma do observador, lembra o js)
    interface Listener {
        //relacionando ao login do user
        fun onUserLoaded(uid: String, user: FBUsuario)
        fun onUserSignOut()
        fun onRegisterSuccess(uid: String)

        //relacionando ações na ocorrencia
        fun onOcorrenciaAdded(ocorrencia: Ocorrencia)
        fun onOcorrenciaUpdated(ocorrencia: Ocorrencia)
        fun onOcorrenciaRemoved(ocorrencia: Ocorrencia)
        //erro
        fun onError(exception : Exception)
    }

    // Acessando o firestore, fazer isso depois de ter o banco
    // private val auth = Firebase.auth
    // private val banco = Firebase.firestore
    //private var ocorrenciasListReg: ListenerRegistration? = null
    private var listener: Listener? = null
    private var authListenerRegistered = false
    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        // Verifica se existe usuário autenticado
        val usuarioAtual = firebaseAuth.currentUser

        if (usuarioAtual == null) {
            ocorrenciasListReg?.remove()
            ocorrenciasListReg = null

            listener?.onUserSignOut()
            return@AuthStateListener
        }

        // Busca os dados do usuário no Firestore
        val refUsuario = banco.collection("usuarios")
            .document(usuarioAtual.uid)

        refUsuario.get()
            .addOnSuccessListener { documento ->
                documento.toObject(FBUsuario::class.java)?.let { usuario ->
                    listener?.onUserLoaded(usuarioAtual.uid, usuario)
                } ?: listener?.onError(
                    Exception("Perfil do usuário não encontrado.")
                )
            }
            .addOnFailureListener { erro ->
                listener?.onError(erro)
            }

        // Observa alterações na coleção de ocorrências
        ocorrenciasListReg?.remove()

        ocorrenciasListReg = banco.collection("ocorrencias")
            .addSnapshotListener { snapshots, erro ->

                if (erro != null) {
                    listener?.onError(erro)
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { change ->

                    val ocorrencia = change.document
                        .toObject(FBOcorrencia::class.java)
                        .toOcorrencia(change.document.id)

                    when (change.type) {
                        DocumentChange.Type.ADDED ->
                            listener?.onOcorrenciaAdded(ocorrencia)

                        DocumentChange.Type.MODIFIED ->
                            listener?.onOcorrenciaUpdated(ocorrencia)

                        DocumentChange.Type.REMOVED ->
                            listener?.onOcorrenciaRemoved(ocorrencia)
                    }
                }
            }
    }

    fun setListener(listener: Listener?) {
        this.listener = listener

        if (listener != null && !authListenerRegistered) {
            auth.addAuthStateListener(authListener)
            authListenerRegistered = true
        }
    }

    // função de register()
    fun register(nome: String, email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { resultado ->

                val uid = resultado.user?.uid

                if (uid == null) {
                    listener?.onError(
                        Exception("Não foi possível obter o UID do usuário.")
                    )
                    return@addOnSuccessListener
                }

                val usuario = FBUsuario(
                    nome = nome,
                    email = email
                )

                banco.collection("usuarios")
                    .document(uid)
                    .set(usuario)
                    .addOnSuccessListener {
                        listener?.onRegisterSuccess(uid)
                    }
                    .addOnFailureListener { erro ->
                        listener?.onError(erro)
                    }
            }
            .addOnFailureListener { erro ->
                listener?.onError(erro)
            }
    }

    //login
    fun login(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnFailureListener { erro ->
                listener?.onError(erro)
            }
    }

    //funções de crud
    fun adicionarOcorrencia(ocorrencia: Ocorrencia) {

        val novaOcorrencia = ocorrencia.toFBOcorrencia()

        banco.collection("ocorrencias")
            .add(novaOcorrencia)
            .addOnFailureListener { erro ->
                listener?.onError(erro)
            }
    }
    fun removerOcorrencia(id: String){
        banco.collection("ocorrencias").document(id).delete().addOnFailureListener { erro ->
            listener?.onError(erro)
        }
    }


    //removendo o listener quando necessário (tipo o logout do usuário VIEWMODEL DEVE O CHAMAR, MAN)
    fun cleanup() {
        if (authListenerRegistered) {
            auth.removeAuthStateListener(authListener)
            authListenerRegistered = false
        }
        ocorrenciasListReg?.remove()
        ocorrenciasListReg = null
        listener = null
    }

    //usuario deslogando
    fun logout() {
        auth.signOut()
    }
}