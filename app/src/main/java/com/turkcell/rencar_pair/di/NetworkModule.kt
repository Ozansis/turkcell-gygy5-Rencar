package com.turkcell.rencar_pair.di

import android.content.Context
import android.content.pm.ApplicationInfo
import com.turkcell.rencar_pair.data.network.AuthApiService
import com.turkcell.rencar_pair.data.network.AuthInterceptor
import com.turkcell.rencar_pair.data.network.CardsApiService
import com.turkcell.rencar_pair.data.network.IyzicoApiService
import com.turkcell.rencar_pair.data.network.LicenseApiService
import com.turkcell.rencar_pair.data.network.RentalsApiService
import com.turkcell.rencar_pair.data.network.ReservationsApiService
import com.turkcell.rencar_pair.data.network.VehiclesApiService
import com.turkcell.rencar_pair.data.network.WalletApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://rencarv2.halitkalayci.com/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(@ApplicationContext context: Context): HttpLoggingInterceptor {
        val isDebuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        return HttpLoggingInterceptor().apply {
            level = if (isDebuggable) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideVehiclesApiService(retrofit: Retrofit): VehiclesApiService {
        return retrofit.create(VehiclesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLicenseApiService(retrofit: Retrofit): LicenseApiService {
        return retrofit.create(LicenseApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideReservationsApiService(retrofit: Retrofit): ReservationsApiService {
        return retrofit.create(ReservationsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRentalsApiService(retrofit: Retrofit): RentalsApiService {
        return retrofit.create(RentalsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWalletApiService(retrofit: Retrofit): WalletApiService {
        return retrofit.create(WalletApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCardsApiService(retrofit: Retrofit): CardsApiService {
        return retrofit.create(CardsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideIyzicoApiService(retrofit: Retrofit): IyzicoApiService {
        return retrofit.create(IyzicoApiService::class.java)
    }
}
