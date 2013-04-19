---
tags: GUI, soapbox
---
On creating a GUI
=================
The Idea for the Project
------------------------
I recently discovered [Notational Velocity](http://notational.net). Since I loved it so much I wanted it on Windows as well. [Lifehacker recommended](http://tbd) [Resophnotes](http://tbd), but somehow I was reluctant to install from yet another unknown site.

For people who dont know what NV is, here's the 2 minute version: NV is a really useful note taking app for the mac. It has a very simple UI that consists of 3 controls - a text box, a list and a text area - and autosave. The 3 controls have multiple purposes:

- The text box is where you type in note titles. Text typed in it is used to search existing notes (by considering it as being substrings of titles) and creating new ones (if the text doesnt match any existing content)
- The list box is where search results show up and can be browsed. Selecting an item here immediately shows its contents in ...
- The text area. As mentioned above, it shows the contents of the selected note (when browsing the search results) or a blank text edit control (when creating a new note) and allows editing and saving it.

Hopefully, this description should suffice for the current essay on creating GUIs; more details can be had at the Notational site or at [my github project that cloned it in Groovy/Java](http://tbd).

First Pass: It looks good!
--------------------------

Having rejected downloading ResophNotes, I thought: its a 3-control UI. How difficult can it be to build it? I know enough Swing to swing (pun partly intended) this one; and to avoid all the Java boilerplate, I'll do it in Groovy. I hear it has a DSL for Swing. If its half as good as the other builder dsls I've been playing around with, this should be easy.

So I init-ed myself a git repo and did a quick mental inventory of things to be built: there was the UI itself, some classes to hold the notes in memory and a means of saving/reloading the notes. Of these, the UI was the one I was least sure of my ability on, so I started with that. I didnt intend to, but I *was* doing [Outside-in development](http://tbd) by default.

Now, the nice thing about GUI development is that you *see* the results of your work. The not-so-nice thing about GUI development is that *because* you can see the results of your work, you spend inordinate amounts of time tweaking minor bits of look and feel that can be spoilt with tiny changes in the environment: a simple resize, for example. So after I had spent enough time in the Valley of Niggling UI tweaks, I finally had the UI looking exactly the way I wanted: a textfield, a listbox and a textarea stacked vertically. Groovy's SwingBuilder worked marvellously well here. The standard Swing way is to create all the child controls, then create the parent and finally add all the children to the parent; the entire process is very bottom-up. Swingbuilder lets you create the parent and nest the children inside directly; so your code reflects the containment hierarchy of the controls and the process is decidedly top-down.

... NOW let's make it do something!
-----------------------------------

Now all I had to do was to get the UI to do something. Easy enough: just add event handlers! Groovy helps you out here, too. Most things that would have required an anonymous inner class, implementing the required listener interfaces in the main class itself or using some Adapter class in Java were available in SwingBuilder as properties that you assign closures to. Nice[1]. The nicer thing, though, was that SwingBuilder exposed the root of the tree of controls as a global variable and allowed specific controls within that tree to be named - either by creating variables in that scope or using the `id` attribute. This allowed for xpath-like expressions to reach specific controls like so: `ui.container1.list3`, which enabled two things:

- Controls become accessible **in their natural scope** for read and write operations directly.
- Behavior could be added **after** the basic UI was built and vetted.

I could imagine my main program being rewritten like so:

	main(){
		createUI();	//
		addBehavior();
		//....
	}

I didnt actually rewrite it just yet that way, but the code was written almost such that it could be wrapped in `createUI()` as part of a rewite in the near future. The alternative was the usual pattern for Swing code:

	class UI{
		
		UI(){}

		private final JSomeControl child;
		private final JSomeControld peer;
		private final JSomeContainer parent = new JSomeContainer(...);

		main(){
			createAndShowUI();
		}


		createAndShowUI(){
			child = new JSomeControl(...);
			peer = ....;

			child.setXXX(xxx);
			child.eventHandler(new EventHandler(Event e){
				// ...
				// accessible because instance var
				peer.setYYY(yyy);
				// ...
			}
			parent.add(child);
			parent.add(peer);
			parent.show();
		}
	}

This felt like a mishmash of UI creation and behavior addition that promised brittleness and misery later. I wanted to build the UI in a modular fashion - so each piece is an easy add-on to what already exists and can be taken out or changed easily if required.

[1]: Well, nice enough. SwingBuilder has its set of annoyances that I've detailed elsewhere, but as a quick-n-dirty tool to create GUIs it was good. 

... but maybe its not all that rosy
-----------------------------------

Once I wrote the first event handler (the text box's `actionPerformed()` would search for notes when the user types some text in the text field and hits enter) I realized two things:

1. To write it in the style that I wanted, event handlers would need to hold references to other controls so that they can either know the source of an event or to trigger an event on other controls.
2. Events are not visual. They need data to show that they worked.

The first point's significance was not apparent to me immediately because SwingBuilder provided those references (and as a hierarchy to boot), but when I set out to rewrite the main program like I'd imagined above in Java, I realized it well enough. More on this later.

The second point meant I had to write some domain classes, but I wanted to defer that for some time more: though it was easy enough to do, I wanted the UI to *actually do something* first. So I thought I'd fake the results and populate the list with dummy data - every search would return back the same set of fake notes. This meant creating a `ListModel` - Swing's own API for separting the "View" from the "Model". But then was this concept of model the same as my domain class? Not necessarily; the list box, for example, needs just a list of note titles. My domain model, on the other hand, has no direct need for such a list. It consists of two concepts:

- A note, which has a title and some contents
- A collection of such notes

As long as I could do CRUD operations on these two types of things, the domain model was satisfied. The "list of note titles" is an artifact of the specific GUI chosen. Clearly it is possible to concieve a conventional menu-based UI that had actions that mapped 1-1 with the domain model: "New Note", "Edit Note", "Delete note" etc come to mind immediately.

There are two kinds of models then: 

- *Logical Models* that are true to the pure domain of notes and their collections, and 
- *View Models* that are aligned with the UI controls used to manipulate the conceptual model.

The line architecture of the app that was a simple 3-tier one like so:

	UI -- Model -- File

...became slightly more complicated, like so:

			UI
	 -------------------
	| Ctrl -- ViewModel | -- LogicalModel -- File
	 -------------------

These diagrams (and the thought process behind them) brought dependencies to sharp focus: The UI couldnt be created without the logical models, which couldnt be created without the persistence mechanism; and within the UI block the controls couldnt be created before their viewmodels were. So my imaginary rewritten main program looked more like this:

	main(){
		lm = createLogicalModels();
		vm = createViewModels(lm);
		ui = createUI(vm);
		ui.addBehavior();
		ui.show();
		//....
	}

... where the arguments being passed in represent dependencies more than just the transfer of values: creating an UI requires that view models are already there, which requires that logical models are there even before and so on.

As I started modifying the code, however, I found *myself* refusing to follow this neatly laid out plan. 

Why the UI could not be built as planned
----------------------------------------

The **first problem** was that of access: just as event handlers need access to other controls, the controls need access to the domain model. This means more "global" variables or a whole lot of get/set calls accross layers.

The **second problem** was that all expected UI behavior was not available in a single UI control; it sometimes required a combination of controls. Case in point: both the search results list and the note contents need to be scrollable. Swing requires that controls needing scrollbars be contained in `JScrollPane`s: scrolling is not a property of a control; it becomes scrollable because it is contained such that it can be scrolled. Now we have a purely UI construct that need not necessarily be available outside `createUI()`. SwingBuilder made this easy enough: dont provide an `ID` attribute and the component is not available (or so I think); but it still remained as a generic problem to the proposed "modular" development.

The **third** and more insidious problem was that the tangle of interactions was hidden within the various event handlers leading to undesirable effects. For eg, I added code in the listbox's `valueChanged()` event handler to display the note title and contents corresponding to the selected item in the list. Unfortunately, this event is also triggered when the list's model changes - something that happened every time the user searches. So the sequence of events was:

	user searches -> listModel.add("title", "contents") -> valueChanged() -> updateTitle() + updateContents()

... while the ONLY intended interaction was:

	user browses list -> valueChanged() -> updateTitle() + updateContents()

The **final problem** was events generated from multiple input devices: keyboard and mouse both generate click, focus and blur events. In a "free" UI such as NV where controls play multiple roles, determining what role the control should play at a given time when the only context available is "event X happened" became limiting.

Given all this, I could hardly get `createUI()` written without infusing behavior into it. So instead I settled for the following compromises:

- To solve the access problem, I created an instance variable mapping to the root of the logical model tree and provided access for the event handlers to it via instance methods.
- The second problem was not a problem per se with the implementation, but I recognized that the overall concept of "create ui, add behavior" would have to allow for "UI-only" components and exposing the tree of UI components similar to SwingBuilder.
- To solve the undesirable effects problem, I simply introduced a state variable that flagged that a search was in progress. That way, `valueChanged()` would fire everytime, but would trigger the updates only when not searching. A kludge, yes, but simple and effective. In a larger program the number of states and their interactions might have been confusing.
- As for the multiple devices problem, I intended to fix it by triggering the changes from those event handlers as well, but also considered the option of just shunning all mouse access as a quick fix: the app was perfectly usable from the keyboard :)

In parallel, however, I was considering the concerns raised on the conceptual design seriously and set out to fix them - in principle, anyway.

So add more design, right?
--------------------------

The implementation to date provided some solutions to the generic concerns:

- Access Problem: Given that controls, view models, logical models and file storage depend on each other in that order and that each layer might need access to arbitrary objects in the next layer, the logical thing to do is to provide a tree of objects at every layer available to the next.
- UI-only components: The tree of objects available to controls need not be all UI controls created; it should instead be a "semantic tree" containing only useful controls.
- Undesirable Effects: The best thing to do here seems to extend the concept of the state variable and maintain the state diagram for the application, with the caveat that we maintain the application's **functional state, not that of the controls**. For eg, the user is either searching or browsing the search results or editing a note. The controls that enable this to happen could be in internal states that we do not care about. Every event handler would act only when the application's state dictates that it should.
- Multiple Devices: Maintaining the application's functional state would solve this problem too: whether the user shift-tabbed twice to arrive at the text field or directly clicked on the control to gain focus, he's just switched into the "search" state.

Now the imaginary rewrite looked like this:

	main(){
		lm = createLogicalModels();
		vm = createViewModels(lm);
		ui = createUI(vm);
		st = createAppStates();
		ui.addBehavior(st);
		ui.show();
		//....
	}

Armed with this design, I set out to rewrite the UI in Java. The reasons for the rewrite [are chronicled elsewhere](README.md), but boy was I ever so....

WRONG!
------

Java's static nature put a big kibosh on most aspects of the design:

* Creating the SwingBuilder's control tree variable was somewhat easy in that all Swing controls are based on `JComponent`, but that didnt make using that data structure any easy. Since Java has no sugar syntax for hashmaps, all declarations were verbose `Hashmap<String, JComponent>`-type deals; and since Java is strongly typed, every access of the control required casting back to its original type (eg, JTextField) before methods applicable to that type were usable.
* Creating the logical model's tree variable was even worse: since these were my classes, I had to create the `JComponent` equivalent - `Model`. Not that it mattered much, though: there is not much similar between model classes except that the ARE model classes. In fact, model classes *should be* unique by definition. Even if the fact that its a model class were marked using an empty interface, it would have little impact because the accessor would have use only for the specific model object being used, not the generic information that it is a model object. So the same issues as above applied here as well: users of model classes needed to know their specific class anyway; the `Model` abstraction brought very little to the table. The only useful bits I could think of were to make a distinction between the models' initial state and their subsequent ones - represented by the `getInitialValue()` and `getCurrentValue()` methods in the `Model` interface; but even those seemed contrived.
* UI-only components were no issue in general. There might be a problem if the externally visible tree had to be autogenerated by visiting the JComponent tree, however.
* Implementing a state machine for the app's UI had two issues. One, that it was an undertaking in and of itself. Existing libraries (like SMC) seemed prohibitively complex - especially when I wanted to keep the app small and dependency-free. Two: it seemed like using a state machine would require specifying all possible states that the app could be in; not just the confusing/problematic ones. The current implementation allowed some standard states - like the initial loaded state to "just be" - something that might not be possible with a state machine; it might require that all such initialization be processed via the event handler for the transition from the "start" state to the "loaded" state, for example.

So...back to square one then?
-----------------------------

I'm not so sure. 

Creating a generic tree structures for UI controls and models only to then require casts back to concrete types before use is definitely pointless. Maybe some reflection/annotation magic is possible, but it seems too meta to me, honestly. I dont have a good answer for Java (or any static language).

State machines to control interactions seems like a good idea, especially when implemented along the lines of [SwingStates](http://swingstates.sourceforge.net/); even without its `Canvas` and `CShape` style of generic GUI coding (which are great but not required for running normal Swing apps), its ideas of state machines, using many of them in the same app and potentially in parallel are sufficiently powerful, IMO.

The largest conceptual doubt is if the "create UI, add behavior" model is indeed *the* way to build a GUI, or the only way. The current "big honking UI class" model definitely seems like it needs to be changed, but is this way to modular UI? Through the course of this article, my imaginary main program changed twice: once to add context variables and then to add a new step. Imagine the sequence of methods being enshrined in a base class that you can only subclass to provide specific implementations for, but never change the actual names of the steps or their sequence. Implementation concerns aside, there are two concerns: each step may have a different set of dependencies (which controls the order of execution) and each UI program might have a slightly different set of steps to be taken. Dependency Injection could probably be used to handle the first of these problems, but the second does need something like an ESB - an obvious overkill by large for a GUI App. But there are more angles: today's rich internet apps rely on lazy loading parts of the UI. How could such an app be written - `createUI()` could still be running but we can call `show()` already?

What remains, then is: building a UI does have steps. There may not be a general set of steps, but each app does have its own set. Making that concrete in the app's code is useful instead of hiding it across multiple control creation routines and event handlers.

Aside: It does seem like an all-encompassing State Machine approach might actually be able to handle all these issues (the lifecycle of the app itself could be modeled as a State Machine), but in practice that might become either too abstract or take on a "life of its own" with the GUI being sidestepped. 

In closing, let me suggest some coding patterns (my term meaning "how to write code"; as opposed to design patterns which are standard solutions to standard problems):

* UI Pipeline/Lifecycle: Determine the sequence of steps required to build your UI and enshrine them as individual steps in your main program.
* Accessing the model/UI tree: If your language allows sugared access to hash-like data structures, create an explicit tree; otherwise create instance/global variables of the useful models/controls.
* Handling interaction: Use state machines when useful. They help consolidate event handling across input devices and output views. If the app is really small, you could get away with a few state variables.
