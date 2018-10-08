package com.akiva.adam.notificator.dagger;

import com.akiva.adam.notificator.activities.MainActivity;
import com.akiva.adam.notificator.services.MainService;
import com.akiva.adam.notificator.classes.Process;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component (modules = {DatabaseModule.class, LocksModule.class})
public interface DatabaseComponent {
    void inject (MainActivity mainActivity);
    void inject (Process process);
    void inject (MainService mainService);
}
