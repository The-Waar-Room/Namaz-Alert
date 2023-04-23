package com.sudoajay.triumph_path.util.di

import android.content.Context
import com.sudoajay.triumph_path.data.proto.ProtoManager
import com.sudoajay.triumph_path.data.repository.WebScrappingGoogle
import com.sudoajay.triumph_path.ui.background.AlarmMangerForTask
import com.sudoajay.triumph_path.ui.notification.AlertNotification
import com.sudoajay.triumph_path.ui.notificationSound.repository.SelectNotificationSoundAdapter
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
    fun providesWorkManger( @ApplicationContext appContext: Context): AlarmMangerForTask = AlarmMangerForTask(appContext)

    @Singleton
    @Provides
    fun providesWebScrappingGoogle( @ApplicationContext appContext: Context): WebScrappingGoogle = WebScrappingGoogle(appContext)

    @Singleton
    @Provides
    fun providesSelectNotificationSoundAdapter( @ApplicationContext appContext: Context): SelectNotificationSoundAdapter = SelectNotificationSoundAdapter(appContext)


}

