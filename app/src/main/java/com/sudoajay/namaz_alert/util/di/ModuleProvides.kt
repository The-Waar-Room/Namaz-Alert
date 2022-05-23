package com.sudoajay.namaz_alert.util.di

import android.content.Context
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.WebScrappingGoogle
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleProvides {

    @Singleton
    @Provides
    fun providesProtoManger( @ApplicationContext appContext: Context): ProtoManager = ProtoManager(appContext)

    @Singleton
    @Provides
    fun providesAlertNotification( @ApplicationContext appContext: Context): AlertNotification = AlertNotification(appContext)

    @Singleton
    @Provides
    fun providesWorkManger( @ApplicationContext appContext: Context): WorkMangerForTask = WorkMangerForTask(appContext)

    @Singleton
    @Provides
    fun providesWebScrappingGoogle( @ApplicationContext appContext: Context): WebScrappingGoogle = WebScrappingGoogle(appContext)



}

