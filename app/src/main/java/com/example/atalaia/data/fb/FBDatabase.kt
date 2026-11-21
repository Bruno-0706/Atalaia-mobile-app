
package com.example.atalaia.data.fb

import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.domain.toOcorrencia

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore


class FBDatabase {
    interface OcorrenciaListener {
        //relacionando ações na ocorrencia
        fun onOcorrenciaAdded(ocorrencia: Ocorrencia)
        fun onOcorrenciaUpdated(ocorrencia: Ocorrencia)
        fun onOcorrenciaRemoved(ocorrencia: Ocorrencia)
        //erro
        fun onError(exception: Exception)
    }

    interface AuthListener {
        //relacionando ao login do user
        fun onUserLoaded(uid: String, user: FBUsuario)
        fun onUserSignOut()
        fun onRegisterSuccess(uid: String)
        //erro
        fun onError(exception: Exception)
    }

    //Acessando o firestore, fazer isso depois de ter o banco
    private val auth = Firebase.auth
    private val banco = Firebase.firestore
    private var ocorrenciasListReg: ListenerRegistration? = null

    private var authCallBack: AuthListener? = null
    private var ocorrenciaCallback: OcorrenciaListener? = null

    private var authListenerRegistered = false

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        // Verifica se existe usuário autenticado
        val usuarioAtual = firebaseAuth.currentUser

        if (usuarioAtual == null) {
            ocorrenciasListReg?.remove()
            ocorrenciasListReg = null

            authCallBack?.onUserSignOut()
            return@AuthStateListener
        }

        // Busca os dados do usuário no Firestore
        val refUsuario = banco.collection("usuarios")
            .document(usuarioAtual.uid)

        refUsuario.get()
            .addOnSuccessListener { documento ->
                documento.toObject(FBUsuario::class.java)?.let { usuario ->
                    authCallBack?.onUserLoaded(usuarioAtual.uid, usuario)
                } ?: authCallBack?.onError(
                    Exception("Perfil do usuário não encontrado.")
                )
            }
            .addOnFailureListener { erro ->
                authCallBack?.onError(erro)
            }

        // Observa alterações na coleção de ocorrências
        ocorrenciasListReg?.remove()

        ocorrenciasListReg = banco.collection("ocorrencias")
            .addSnapshotListener { snapshots, erro ->

                if (erro != null) {
                    ocorrenciaCallback?.onError(erro)
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { change ->

                    val ocorrencia = change.document
                        .toObject(FBOcorrencia::class.java)
                        .toOcorrencia(change.document.id)

                    when (change.type) {
                        DocumentChange.Type.ADDED ->
                            ocorrenciaCallback?.onOcorrenciaAdded(ocorrencia)

                        DocumentChange.Type.MODIFIED ->
                            ocorrenciaCallback?.onOcorrenciaUpdated(ocorrencia)

                        DocumentChange.Type.REMOVED ->
                            ocorrenciaCallback?.onOcorrenciaRemoved(ocorrencia)
                    }
                }
            }
    }

    fun setAuthListener(listener: AuthListener?) {
        authCallBack = listener

        if (listener != null && !authListenerRegistered) {
            auth.addAuthStateListener(authListener)
            authListenerRegistered = true
        }
    }

    fun setOcorrenciaListener(listener: OcorrenciaListener?) {
        ocorrenciaCallback = listener
    }

    // função de register()
    fun register(nome: String, email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { resultado ->

                val uid = resultado.user?.uid

                if (uid == null) {
                    authCallBack?.onError(
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
                        authCallBack?.onRegisterSuccess(uid)
                    }
                    .addOnFailureListener { erro ->
                        authCallBack?.onError(erro)
                    }
            }
            .addOnFailureListener { erro ->
                authCallBack?.onError(erro)
            }
    }

    //login
    fun login(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnFailureListener { erro ->
                authCallBack?.onError(erro)
            }
    }

    //funções de crud
    fun adicionarOcorrencia(ocorrencia: Ocorrencia) {

        val novaOcorrencia = ocorrencia.toFBOcorrencia()

        banco.collection("ocorrencias")
            .add(novaOcorrencia)
            .addOnFailureListener { erro ->
                ocorrenciaCallback?.onError(erro)
            }
    }

    fun removerOcorrencia(id: String) {
        banco.collection("ocorrencias")
            .document(id)
            .delete()
            .addOnFailureListener { erro ->
                ocorrenciaCallback?.onError(erro)
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

        authCallBack = null
        ocorrenciaCallback = null
    }

    //usuario deslogando
    fun logout() {
        auth.signOut()
    }
}
