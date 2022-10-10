package com.example.mynotes.di

import android.provider.ContactsContract
import com.example.mynotes.apis.Authintercepter
import com.example.mynotes.apis.NotesAPI
import com.example.mynotes.apis.UserAPI
import com.example.mynotes.utils.Constants
import com.example.mynotes.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofitObject() : Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)

    }

    @Singleton
    @Provides
    fun provideHttpClient(authintercepter: Authintercepter) : OkHttpClient
    {
        return OkHttpClient.Builder().addInterceptor(authintercepter).build()
    }

    @Singleton
    @Provides
    fun provideUserAPI(retrofitBuilder: Retrofit.Builder) : UserAPI {
        return retrofitBuilder.build().create(UserAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideNoteAPI(retrofitBuilder: Retrofit.Builder,okHttpClient: OkHttpClient) : NotesAPI {
        return retrofitBuilder
            .client(okHttpClient)
            .build().create(NotesAPI::class.java)
    }


}