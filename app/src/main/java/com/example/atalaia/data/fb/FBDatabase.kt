package com.example.atalaia.data.fb

import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.domain.toOcorrencia
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.example.atalaia.domain.Tag

class FBDatabase {

    interface OcorrenciaListener {
        fun onOcorrenciaAdded(ocorrencia: Ocorrencia)
        fun onOcorrenciaUpdated(ocorrencia: Ocorrencia)
        fun onOcorrenciaRemoved(ocorrencia: Ocorrencia)
        fun onError(exception: Exception)
    }

    interface AuthListener {
        fun onUserLoaded(uid: String, user: FBUsuario)
        fun onUserSignOut()
        fun onRegisterSuccess(uid: String)
        fun onError(exception: Exception)
    }

    interface TagListener {
        fun onTagsChanged(tags: List<Tag>)
        fun onError(exception: Exception)
    }

    private val auth = Firebase.auth
    private val banco = Firebase.firestore

    private var authCallBack: AuthListener? = null
    private var ocorrenciaCallback: OcorrenciaListener? = null

    private var authListenerRegistered = false
    private var authListener: FirebaseAuth.AuthStateListener? = null

    private var ocorrenciasListReg: ListenerRegistration? = null

    private var tagCallback: TagListener? = null
    private var tagsListReg: ListenerRegistration? = null

    // --------------------------------------------------
    // AUTENTICAÇÃO
    // --------------------------------------------------

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->

        val usuarioAtual = firebaseAuth.currentUser

        if (usuarioAtual == null) {
            authCallBack?.onUserSignOut()
            return@AuthStateListener
        }

        banco.collection("usuarios")
            .document(usuarioAtual.uid)
            .get()
            .addOnSuccessListener { documento ->

                // Evita utilizar um resultado de uma sessão anterior.
                if (auth.currentUser?.uid != usuarioAtual.uid) {
                    return@addOnSuccessListener
                }

                val usuario = documento.toObject(FBUsuario::class.java)

                if (usuario != null) {
                    authCallBack?.onUserLoaded(usuarioAtual.uid, usuario)
                } else {
                    authCallBack?.onError(
                        Exception("Perfil do usuário não encontrado.")
                    )
                }
            }
            .addOnFailureListener { erro ->
                authCallBack?.onError(erro)
            }
    }

    fun setAuthListener(listener: AuthListener?) {
        authCallBack = listener

        if (listener != null && !authListenerRegistered) {
            auth.addAuthStateListener(authStateListener)
            authListenerRegistered = true
        }
    }

    fun removerAuthListener() {
        if (authListenerRegistered) {
            auth.removeAuthStateListener(authStateListener)
            authListenerRegistered = false
        }

        authCallBack = null
    }

    // --------------------------------------------------
    // OCORRÊNCIAS
    // --------------------------------------------------

    fun setOcorrenciaListener(listener: OcorrenciaListener?) {
        ocorrenciaCallback = listener

        if (listener != null && ocorrenciasListReg == null) {
            observarOcorrencias()
        } else if (listener == null) {
            removerOcorrenciaListener()
        }
    }

    private fun observarOcorrencias() {

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

    fun removerOcorrenciaListener() {
        ocorrenciasListReg?.remove()
        ocorrenciasListReg = null
        ocorrenciaCallback = null
    }

    // --------------------------------------------------
    // USUÁRIO
    // --------------------------------------------------

    fun usuarioAtualId(): String? {
        return auth.currentUser?.uid
    }

    fun register(nome: String, email: String, senha: String) {

        auth.createUserWithEmailAndPassword(email.trim(), senha)
            .addOnSuccessListener { resultado ->

                val uid = resultado.user?.uid

                if (uid == null) {
                    authCallBack?.onError(
                        Exception("Não foi possível obter o UID do usuário.")
                    )
                    return@addOnSuccessListener
                }

                val usuario = FBUsuario(
                    nome = nome.trim(),
                    email = email.trim()
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

    fun login(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email.trim(), senha)
            .addOnFailureListener { erro ->
                authCallBack?.onError(erro)
            }
    }

    fun logout() {
        auth.signOut()
    }

    // --------------------------------------------------
    // CRUD DE OCORRÊNCIAS
    // --------------------------------------------------

    fun adicionarOcorrencia(
        ocorrencia: Ocorrencia,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val novaOcorrencia = ocorrencia.toFBOcorrencia()

        banco.collection("ocorrencias")
            .document(ocorrencia.id)
            .set(novaOcorrencia)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { erro ->
                onFailure(erro)
            }
    }

    fun atualizarOcorrencia(
        ocorrencia: Ocorrencia,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val ocorrenciaAtualizada = ocorrencia.toFBOcorrencia()

        banco.collection("ocorrencias")
            .document(ocorrencia.id)
            .set(ocorrenciaAtualizada)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { erro ->
                onFailure(erro)
            }
    }

    fun removerOcorrencia(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        banco.collection("ocorrencias")
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { erro ->
                onFailure(erro)
            }
    }

    // --------------------------------------------------
    // TAGS
    // --------------------------------------------------

    fun setTagListener(listener: TagListener?) {
        tagCallback = listener

        if (listener != null && tagsListReg == null) {
            observarTags()
        } else if (listener == null) {
            removerTagListener()
        }
    }

    private fun observarTags() {

        tagsListReg = banco.collection("tags")
            .addSnapshotListener { snapshots, erro ->

                if (erro != null) {
                    tagCallback?.onError(erro)
                    return@addSnapshotListener
                }

                val tags = snapshots?.documents
                    ?.mapNotNull { documento ->
                        documento.toObject(FBTag::class.java)
                            ?.takeIf { !it.nome.isNullOrBlank() }
                            ?.toTag(documento.id)
                    }
                    ?.sortedBy { it.nome.lowercase() }
                    ?: emptyList()

                tagCallback?.onTagsChanged(tags)
            }
    }

    fun removerTagListener() {
        tagsListReg?.remove()
        tagsListReg = null
        tagCallback = null
    }

    // --------------------------------------------------
    // LIMPEZA GERAL
    // --------------------------------------------------

    fun cleanup() {
        removerAuthListener()
        removerOcorrenciaListener()
        removerTagListener()
    }
}