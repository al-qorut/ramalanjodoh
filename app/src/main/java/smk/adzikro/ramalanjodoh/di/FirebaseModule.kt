package smk.adzikro.ramalanjodoh.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import smk.adzikro.ramalanjodoh.data.datasource.LocalDatasource
import smk.adzikro.ramalanjodoh.data.datasource.RemoteDatasource
import smk.adzikro.ramalanjodoh.data.local.DbRamal
import smk.adzikro.ramalanjodoh.data.remote.FireStore
import smk.adzikro.ramalanjodoh.data.repo.RepoImpl
import smk.adzikro.ramalanjodoh.data.repo.Repositories
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestore(@ApplicationContext
        context: Context,
        firestore: FirebaseFirestore): FireStore {
        return FireStore(context, firestore)
    }



    @Provides
    @Singleton
    fun provideRepositories(remoteDatasource: RemoteDatasource, localDatasource: LocalDatasource): Repositories {
        return RepoImpl(remoteDatasource, localDatasource)
    }




}