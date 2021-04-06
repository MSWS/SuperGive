package xyz.msws.supergive.modules;

import xyz.msws.supergive.SuperGive;

public abstract class AbstractModule {
    protected String id;
    protected boolean enabled;
    protected SuperGive plugin;

    public AbstractModule(SuperGive plugin) {
        this.plugin = plugin;
    }

    public AbstractModule(String id, SuperGive plugin) {
        this.id = id;
        this.plugin = plugin;
    }

    public abstract void initialize();

    public abstract void disable();

    public abstract ModulePriority getPriority();

    public String getId() {
        return id;
    }

    public void setEnabled(boolean enable) {
        enabled = enable;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
