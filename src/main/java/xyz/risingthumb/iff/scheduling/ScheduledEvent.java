package xyz.risingthumb.iff.scheduling;

// This is your event. You can create a Scheduler, and create your own instance of this and override the run with whatever you want
public abstract class ScheduledEvent {
	public abstract void run();
}
