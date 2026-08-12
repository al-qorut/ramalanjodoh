package smk.adzikro.ramalanjodoh.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import smk.adzikro.ramalanjodoh.data.local.DbRamal
import smk.adzikro.ramalanjodoh.data.models.RamalDao
import smk.adzikro.ramalanjodoh.data.repo.Ramalimpl
import smk.adzikro.ramalanjodoh.data.repo.RepoRamal

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideRamalDatabase(app: Application): DbRamal =
        DbRamal.getInstance(app)!!
        //Room.databaseBuilder(
        //    app, DbRamal::class.java,"dataramal.db").build()

    @Provides
    @Singleton
    fun provideRamalDao(db: DbRamal) : RamalDao = db.ramal()

    @Provides
    @Singleton
    fun provideRamalRepository(@ApplicationContext context: Context, db: RamalDao) : RepoRamal = Ramalimpl(context, db)

}
