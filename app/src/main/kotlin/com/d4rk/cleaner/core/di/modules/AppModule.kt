package com.d4rk.cleaner.core.di.modules

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.d4rk.android.libs.apptoolkit.app.main.domain.usecases.PerformInAppUpdateUseCase
import com.d4rk.android.libs.apptoolkit.app.oboarding.utils.interfaces.providers.OnboardingProvider
import com.d4rk.android.libs.apptoolkit.data.client.KtorClient
import com.d4rk.android.libs.apptoolkit.data.core.ads.AdsCoreManager
import com.d4rk.cleaner.R
import com.d4rk.cleaner.app.apps.manager.data.ApkFileManagerImpl
import com.d4rk.cleaner.app.apps.manager.data.AppPackageManagerImpl
import com.d4rk.cleaner.app.apps.manager.data.PackageManagerFacadeImpl
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.ApkFileManager
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.ApkInstaller
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.ApkSharer
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.AppInfoOpener
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.AppSharer
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.AppUninstaller
import com.d4rk.cleaner.app.apps.manager.domain.interfaces.PackageManagerFacade
import com.d4rk.cleaner.app.apps.manager.domain.usecases.GetApkFilesFromStorageUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.GetInstalledAppsUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.InstallApkUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.OpenAppInfoUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.ShareApkUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.ShareAppUseCase
import com.d4rk.cleaner.app.apps.manager.domain.usecases.UninstallAppUseCase
import com.d4rk.cleaner.app.apps.manager.ui.AppManagerViewModel
import com.d4rk.cleaner.app.clean.memory.data.MemoryRepositoryImpl
import com.d4rk.cleaner.app.clean.memory.domain.interfaces.MemoryRepository
import com.d4rk.cleaner.app.clean.memory.domain.usecases.GetRamInfoUseCase
import com.d4rk.cleaner.app.clean.memory.domain.usecases.GetStorageInfoUseCase
import com.d4rk.cleaner.app.clean.memory.ui.MemoryManagerViewModel
import com.d4rk.cleaner.app.clean.scanner.data.ScannerRepositoryImpl
import com.d4rk.cleaner.app.clean.scanner.domain.`interface`.ScannerRepositoryInterface
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.AnalyzeFilesUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.DeleteFilesUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.GetFileTypesUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.MoveToTrashUseCase
import com.d4rk.cleaner.app.clean.scanner.domain.usecases.UpdateTrashSizeUseCase
import com.d4rk.cleaner.app.clean.scanner.ui.ScannerViewModel
import com.d4rk.cleaner.app.clean.trash.domain.usecases.GetTrashFilesUseCase
import com.d4rk.cleaner.app.clean.trash.domain.usecases.RestoreFromTrashUseCase
import com.d4rk.cleaner.app.clean.trash.ui.TrashViewModel
import com.d4rk.cleaner.app.images.compressor.domain.usecases.CompressImageUseCase
import com.d4rk.cleaner.app.images.compressor.domain.usecases.GetImageDimensionsUseCase
import com.d4rk.cleaner.app.images.compressor.domain.usecases.GetOptimizedDestinationFileUseCase
import com.d4rk.cleaner.app.images.compressor.domain.usecases.GetRealFileFromUriUseCase
import com.d4rk.cleaner.app.images.compressor.ui.ImageOptimizerViewModel
import com.d4rk.cleaner.app.main.ui.MainViewModel
import com.d4rk.cleaner.app.onboarding.utils.interfaces.providers.AppOnboardingProvider
import com.d4rk.cleaner.core.data.datastore.DataStore
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule : Module = module {
    single<DataStore> { DataStore(context = get()) }
    single<AdsCoreManager> { AdsCoreManager(context = get() , buildInfoProvider = get()) }
    single { KtorClient().createClient() }

    single<List<String>>(qualifier = named(name = "startup_entries")) {
        get<Context>().resources.getStringArray(R.array.preference_startup_entries).toList()
    }

    single<List<String>>(qualifier = named(name = "startup_values")) {
        get<Context>().resources.getStringArray(R.array.preference_startup_values).toList()
    }

    single<OnboardingProvider> { AppOnboardingProvider() }

    single<AppUpdateManager> { AppUpdateManagerFactory.create(get()) }
    factory<PerformInAppUpdateUseCase> { (launcher : ActivityResultLauncher<IntentSenderRequest>) ->
        PerformInAppUpdateUseCase(appUpdateManager = get() , updateResultLauncher = launcher)
    }

    viewModel<MainViewModel> { (launcher : ActivityResultLauncher<IntentSenderRequest>) ->
        MainViewModel(performInAppUpdateUseCase = get { parametersOf(launcher) })
    }

    single<ScannerRepositoryInterface> { ScannerRepositoryImpl(application = get() , dataStore = get()) }
    single<com.d4rk.cleaner.app.clean.scanner.domain.usecases.GetStorageInfoUseCase> { com.d4rk.cleaner.app.clean.scanner.domain.usecases.GetStorageInfoUseCase(homeRepository = get()) }
    single<GetFileTypesUseCase> { GetFileTypesUseCase(homeRepository = get()) }
    single<AnalyzeFilesUseCase> { AnalyzeFilesUseCase(homeRepository = get()) }
    single<DeleteFilesUseCase> { DeleteFilesUseCase(homeRepository = get()) }
    single<MoveToTrashUseCase> { MoveToTrashUseCase(homeRepository = get()) }
    single<UpdateTrashSizeUseCase> { UpdateTrashSizeUseCase(homeRepository = get()) }

    viewModel<ScannerViewModel> {
        ScannerViewModel(
            application = get(),
            getStorageInfoUseCase = get(),
            getFileTypesUseCase = get(),
            analyzeFilesUseCase = get(),
            deleteFilesUseCase = get(),
            moveToTrashUseCase = get(),
            updateTrashSizeUseCase = get(),
            dispatchers = get(),
            dataStore = get()
        )
    }

    single<PackageManagerFacade> { PackageManagerFacadeImpl(application = get()) }
    single<GetInstalledAppsUseCase> { GetInstalledAppsUseCase(packageManagerFacade = get()) }
    single<ApkFileManager> { ApkFileManagerImpl(application = get()) }
    single<GetApkFilesFromStorageUseCase> { GetApkFilesFromStorageUseCase(apkFileManager = get()) }
    single<AppPackageManagerImpl> { AppPackageManagerImpl(application = get()) }
    single<ApkInstaller> { get<AppPackageManagerImpl>() }
    single<ApkSharer> { get<AppPackageManagerImpl>() }
    single<AppSharer> { get<AppPackageManagerImpl>() }
    single<AppInfoOpener> { get<AppPackageManagerImpl>() }
    single<AppUninstaller> { get<AppPackageManagerImpl>() }
    single<InstallApkUseCase> { InstallApkUseCase(apkInstaller = get()) }
    single<ShareApkUseCase> { ShareApkUseCase(apkSharer = get()) }
    single<ShareAppUseCase> { ShareAppUseCase(appSharer = get()) }
    single<OpenAppInfoUseCase> { OpenAppInfoUseCase(appInfoOpener = get()) }
    single<UninstallAppUseCase> { UninstallAppUseCase(appUninstaller = get()) }

    viewModel<AppManagerViewModel> {
        AppManagerViewModel(application = get() , getInstalledAppsUseCase = get() , getApkFilesFromStorageUseCase = get() , installApkUseCase = get() , shareApkUseCase = get() , shareAppUseCase = get() , openAppInfoUseCase = get() , uninstallAppUseCase = get() , dispatchers = get())
    }

    single<MemoryRepository> { MemoryRepositoryImpl(application = get()) }
    single<GetStorageInfoUseCase> { GetStorageInfoUseCase(memoryRepository = get()) }
    single<GetRamInfoUseCase> { GetRamInfoUseCase(memoryRepository = get()) }

    viewModel<MemoryManagerViewModel> {
        MemoryManagerViewModel(getStorageInfoUseCase = get() , getRamInfoUseCase = get() , dispatchers = get())
    }

    single<GetTrashFilesUseCase> { GetTrashFilesUseCase(repository = get()) }
    single<RestoreFromTrashUseCase> { RestoreFromTrashUseCase(repository = get()) }
    viewModel<TrashViewModel> {
        TrashViewModel(
            getTrashFilesUseCase = get(),
            deleteFilesUseCase = get(),
            updateTrashSizeUseCase = get(),
            restoreFromTrashUseCase = get(),
            dispatchers = get(),
            dataStore = get()
        )
    }

    single<CompressImageUseCase> { CompressImageUseCase(context = get()) }
    single<GetRealFileFromUriUseCase> { GetRealFileFromUriUseCase(context = get()) }
    single<GetImageDimensionsUseCase> { GetImageDimensionsUseCase() }
    single<GetOptimizedDestinationFileUseCase> { GetOptimizedDestinationFileUseCase() }
    viewModel<ImageOptimizerViewModel> {
        ImageOptimizerViewModel(
            application = get(),
            compressImageUseCase = get(),
            getRealFileFromUriUseCase = get(),
            getImageDimensionsUseCase = get(),
            getDestinationFileUseCase = get()
        )
    }
}