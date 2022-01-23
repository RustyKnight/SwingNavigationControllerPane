# SwingNavigationControllerPane

**This is an experiment** using Swing to produce a single window navigation controller capable of pushing and poping "views".

The intention is to provide a workflow which is decoupled, so the indiviual views don't care about "how" the navigation gets done, their only responsibility is to notify the observer(s) that some state has changed, which in turn may trigger a navigation to a new view.

The API also provides a "optional" `interface` for views that might want/need to know when a navigation is taking place

<img src="Navi01.gif">

# Requirements

![Java](https://img.shields.io/badge/Java-16.0.2-orange) ![Netbeans](https://img.shields.io/badge/Netbeans-12.4-orange)

# Why?

Why not?  It's cool!  I mean, just look at it, it's cool!

# But what about `CardLayout`?

Good question!  What about it?

Seriously, `CardLayout` is a great option for switching between known views.  It's not very good and transitioning state through those views, for example, a login or registration flow, where data might need to pass from one view to another.  It can be done, but I don't find it all the easy to do.

`CardLayout` also requires all the views to be instansiated up-front (again, you "can" get around it, but it's kind of messy)

Again, this is an **experiment!**.  Besides, it looks cool!

# Enhancements

This is a first draft attempt.  I'd like the "model" to be decoupled from the "navigation pane" more, so I don't need to extend directly from the `NavigationPane` when ever I want to implement a naviagtion workflow.  So instead, we could define a model which defined the navigation requirements for a group of views and could then be applied to a instance of `NavigationPane`.

The short comming to this basically boils down to the need for the "model" to generate "components" which can be pushed.  The model really shouldn't be creating visual elements.  It might be possible to, instead, bind the "model" with the "navigation pane" through the use of generics and use a "factory" to actually generate the physical components.  So the model would instruct the navigation pane to "push a view" and the navigation pane would ask the factory for a visiual component which represented the models concept of a "view".

The model would also need to provide some kind of observer pattern which would be used to notify, in paritcular, the navigation pane when a view was pushed or popped...and frankly getting the animation to work was hard enough 🤪
