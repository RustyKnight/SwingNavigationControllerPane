/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kaizen.navigationController;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.Duration;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.kaizen.animation.Animatable;
import org.kaizen.animation.AnimatableAdapter;
import org.kaizen.animation.AnimatableDuration;
import org.kaizen.animation.DefaultAnimatableDuration;
import org.kaizen.animation.curves.Curves;
import org.kaizen.animation.ranges.FloatRange;
import org.kaizen.animation.ranges.IntRange;
import org.kaizen.animation.ranges.Range;

/**
 * Why is this contained within a component?  Why isn't separated into
 * a controller/model?
 * 
 * The primary answer is, it's complicated.  Try to animate the transition
 * is actually really complicated.  Realistically, it should be using
 * a custom layout manager to perform these operations.  I've been able
 * to "hack" around it slightly, but it's still one of those "this will
 * go wrong badly" situations.
 * 
 * In theory, we could separate the model, but this adds another layer
 * of complexity, with the need to introduce an observer pattern to 
 * monitor the various changes that will occur - not saying it's
 * a bad idea, it's just one more level of complexity
 * 
 * Another consideration is in how the animation is working.  In order to
 * make the fade work, we need to implement a "proxy" component, so, 
 * the layout manager would not only be managing the position of the
 * component but also its transparency ... not really its job
 * 
 * You also end up at a point where the model wants to push a component,
 * not really it's job
 */
//public class ViewNavigationModelledPane<View> extends JPanel {
//
//    protected enum TransitionType {
//        PUSH, POP;
//    }
//
//    // I'd like to replace this with some kind of mapping between the
//    // View and the component, so that I can simply lookup the view
//    // based on what the model is telling me, instead of mainatining
//    // my own stack
//    private FILOStack<ViewProxyPane> viewStack;
//    
//    private ViewNavigationModel<View> model;
//    private NavigationControllerViewFactory<View> viewFactory;
//    
//    // Look, I'm not keen on exposing the conformance of this obsever directly
//    private ViewNavigationModel.Listener<View> modelListener = new ViewNavigationModel.Listener<View>() {
//        @Override
//        public void didPush(ViewNavigationModel<View> source, View view) {
//            modelDidPush(view);
//        }
//
//        @Override
//        public void didPop(ViewNavigationModel<View> source, View view) {
//            modelDidPop(view);
//        }
//
//        @Override
//        public void didReplaceView(ViewNavigationModel<View> source, View view) {
//            modelDidReplaceView(view);
//        }
//
//        @Override
//        public void didPopToRoot(ViewNavigationModel<View> source, View oldView, View newView) {
//            popToRoot(oldView, newView);
//        }
//    };
//
//    public ViewNavigationModelledPane() {
//        viewStack = new FILOStack<>();
//    }
//
//    public void setModel(ViewNavigationModel<View> model) {
//        if (this.model != null) {
//            this.model.removeListener(modelListener);
//        }
//        this.model = model;
//        this.model.addListener(modelListener);
//        
//        View view = model.last();
//        if (view != null) {
//            setRoot(view);
//        }
//    }
//
//    public void setViewFactory(NavigationControllerViewFactory<View> viewFactory) {
//        this.viewFactory = viewFactory;
//    }
//
//    public ViewNavigationModel<View> getModel() {
//        return model;
//    }
//
//    public NavigationControllerViewFactory<View> getViewFactory() {
//        return viewFactory;
//    }
//
//    protected NavigatableView navigatableView(ViewProxyPane view) {
//        if (view != null && view.getView() instanceof NavigatableView) {
//            return (NavigatableView) view.getView();
//        }
//        return null;
//    }
//
//    protected void willPresent(ViewProxyPane view) {
//        NavigatableView delegate = navigatableView(view);
//        if (delegate == null) {
//            return;
//        }
//        delegate.willPresent();
//    }
//
//    protected void didPresent(ViewProxyPane view) {
//        NavigatableView delegate = navigatableView(view);
//        if (delegate == null) {
//            return;
//        }
//        delegate.didPresent();
//    }
//
//    protected void willDismiss(ViewProxyPane view) {
//        NavigatableView delegate = navigatableView(view);
//        if (delegate == null) {
//            return;
//        }
//        delegate.willDismiss();
//    }
//
//    protected void didDismiss(ViewProxyPane view) {
//        NavigatableView delegate = navigatableView(view);
//        if (delegate == null) {
//            return;
//        }
//        delegate.didDismiss();
//    }
//    
//    protected void modelDidPush(View view) {
//        JComponent component = getViewFactory().getComponentForView(view, getModel().getContext());
//        push(component);
//    }
//    
//    protected void modelDidPop(View view) {
//        pop();
//    }
//    
//    protected void modelDidReplaceView(View view) {
//        replaceWith(getViewFactory().getComponentForView(view, getModel().getContext()));
//    }
//    
//    // This will reset the navigation controller, removing all previous views
//    // and applying the specified view as the "root" view
//    protected void setRoot(View view) {
//        setRoot(getViewFactory().getComponentForView(view, getModel().getContext()));
//    }
//    
//    protected void setRoot(JComponent component) {
//        ViewProxyPane lastView = viewStack.pop();
//        willDismiss(lastView);
//        removeAll();
//        didDismiss(lastView);
//        
//        viewStack.clear();
//        
//        if (component != null) {
//            ViewProxyPane proxyPane = new ViewProxyPane(component);
//            viewStack.push(proxyPane);
//            
//            willPresent(proxyPane);
//            add(proxyPane);
//            revalidate();
//            repaint();
//            didPresent(proxyPane);
//        }
//    }
//
//    protected void push(JComponent view) {
//        ViewProxyPane currentView = viewStack.peekLast();
//        willDismiss(currentView);
//
//        ViewProxyPane nextView = new ViewProxyPane(view);
//
//        viewStack.push(nextView);
//
//        transitionBetween(currentView, nextView, TransitionType.PUSH);
//    }
//
//    protected void pop() {
//        ViewProxyPane currentView = viewStack.pop();
//        ViewProxyPane nextView = viewStack.peekLast();
//
//        if (nextView != null) {
//            transitionBetween(currentView, nextView, TransitionType.POP);
//        } else {
//            willDismiss(currentView);
//            removeAll();
//            didDismiss(currentView);
//        }
//    }
//    
//    protected void popToRoot(View oldView, View newView) {
//        popToRoot();
//    }
//
//    protected void popToRoot() {
//        ViewProxyPane rootView = viewStack.peekFirst();
//        ViewProxyPane currentView = viewStack.peekLast();
//        // If first is null, last will be null
//        if (rootView == null) {
//            return;
//        }
//
//        while (viewStack.peekLast() != rootView) {
//            viewStack.pop();
//        }
//
//        transitionBetween(currentView, rootView, TransitionType.POP);
//    }
//
//    protected void replaceWith(JComponent view) {
//        ViewProxyPane last = viewStack.peekLast();
//        push(view);
//        if (last != null) {
//            viewStack.remove(last);
//        }
//    }
//
//    protected void transitionBetween(ViewProxyPane oldView, ViewProxyPane newView, TransitionType type) {
//        willDismiss(oldView);
//        willPresent(newView);
//
//        switch (type) {
//            case PUSH:
//                transitionPushBetween(oldView, newView);
//                break;
//            case POP:
//                transitionPopBetween(oldView, newView);
//                break;
//        }
//    }
//
//    protected void didTransitionBetween(ViewProxyPane oldView, ViewProxyPane newView, TransitionType type) {
//        switch (type) {
//            case PUSH:
//                didTransitionBetweenPush(oldView, newView);
//                break;
//            case POP:
//                didTransitionBetweenPop(oldView, newView);
//                break;
//        }
//    }
//
//    protected void didTransitionBetweenPush(ViewProxyPane oldView, ViewProxyPane newView) {
//        didDismiss(oldView);
//        didPresent(newView);
//    }
//
//    protected void didTransitionBetweenPop(ViewProxyPane oldView, ViewProxyPane newView) {
//        didDismiss(oldView);
//        didPresent(newView);
//    }
//
//    protected void animateTransition(TransitionType type, AnimtionTransitionProperties oldViewProperties, AnimtionTransitionProperties newViewProperties) {
//        newViewProperties.getView().setAlpha(0);
//        add(newViewProperties.getView());
//
//        revalidate();
//
//        Duration duration = Duration.ofMillis(300);
////            Duration duration = Duration.ofSeconds(5);
//
//        DefaultAnimatableDuration animator = new DefaultAnimatableDuration(duration, Curves.SINE_IN_OUT.getCurve(), new AnimatableAdapter() {
//            @Override
//            public void animationCompleted(Animatable animator) {
//                ViewProxyPane oldView = oldViewProperties.getView();
//                ViewProxyPane newView = newViewProperties.getView();
//                remove(oldView);
//                // Reset the alpha values :P
//                newView.setAlpha(1);
//                oldView.setAlpha(1);
//
//                didTransitionBetween(oldView, newView, type);
//
//                revalidate();
//                repaint();
//            }
//
//            @Override
//            public void animationTimeChanged(AnimatableDuration animatable) {
//                double progress = animatable.getProgress();
//
//                ViewProxyPane newView = newViewProperties.getView();
//                Range<Float> alphaInRange = newViewProperties.getAlphaRange();
//                Range<Integer> moveInRange = newViewProperties.getMovementRange();
//
//                newView.setLocation(moveInRange.valueAt(progress), 0);
//                newView.setAlpha(alphaInRange.valueAt(progress));
//
//                ViewProxyPane oldView = oldViewProperties.getView();
//                Range<Float> alphaOutRange = oldViewProperties.getAlphaRange();
//                Range<Integer> moveOutRange = oldViewProperties.getMovementRange();
//
//                oldView.setLocation(moveOutRange.valueAt(progress), 0);
//                oldView.setAlpha(alphaOutRange.valueAt(progress));
//            }
//        });
//        animator.start();
//    }
//
//    protected void transitionPopBetween(ViewProxyPane oldView, ViewProxyPane newView) {
//        int distant = (int) (getWidth() * 0.25);
//
//        Range<Float> alphaInRange = new FloatRange(0f, 1f);
//        Range<Integer> moveInRange = new IntRange(-distant, 0);
//
//        Range<Float> alphaOutRange = new FloatRange(1f, 0f);
//        Range<Integer> moveOutRange = new IntRange(0, distant);
//
//        AnimtionTransitionProperties newAnimtionProperties = new AnimtionTransitionProperties(newView, alphaInRange, moveInRange);
//        AnimtionTransitionProperties oldAnimtionProperties = new AnimtionTransitionProperties(oldView, alphaOutRange, moveOutRange);
//
//        animateTransition(TransitionType.POP, oldAnimtionProperties, newAnimtionProperties);
//    }
//
//    protected void transitionPushBetween(ViewProxyPane oldView, ViewProxyPane newView) {
//        if (oldView == null) {
//            add(newView);
//            didTransitionBetweenPush(oldView, newView);
//            revalidate();
//            repaint();
//            return;
//        }
//
//        int distant = (int) (getWidth() * 0.25);
//
//        Range<Float> alphaInRange = new FloatRange(0f, 1f);
//        Range<Integer> moveInRange = new IntRange(distant, 0);
//
//        Range<Float> alphaOutRange = new FloatRange(1f, 0f);
//        Range<Integer> moveOutRange = new IntRange(0, -distant);
//
//        AnimtionTransitionProperties newAnimtionProperties = new AnimtionTransitionProperties(newView, alphaInRange, moveInRange);
//        AnimtionTransitionProperties oldAnimtionProperties = new AnimtionTransitionProperties(oldView, alphaOutRange, moveOutRange);
//
//        animateTransition(TransitionType.PUSH, oldAnimtionProperties, newAnimtionProperties);
//    }
//
//    protected class AnimtionTransitionProperties {
//
//        private ViewProxyPane view;
//        private Range<Float> alphaRange;
//        private Range<Integer> movementRange;
//
//        public AnimtionTransitionProperties(ViewProxyPane view, Range<Float> alphaRange, Range<Integer> movementRange) {
//            this.view = view;
//            this.alphaRange = alphaRange;
//            this.movementRange = movementRange;
//        }
//
//        public ViewProxyPane getView() {
//            return view;
//        }
//
//        public Range<Float> getAlphaRange() {
//            return alphaRange;
//        }
//
//        public Range<Integer> getMovementRange() {
//            return movementRange;
//        }
//    }
//
//    protected class ViewProxyPane extends JPanel {
//
//        private float alpha = 1.0f;
//
//        private JComponent view;
//
//        public ViewProxyPane(JComponent view) {
//            setOpaque(false);
//            this.view = view;
//
//            setLayout(new BorderLayout());
//            add(view);
//        }
//
//        public JComponent getView() {
//            return view;
//        }
//
//        public float getAlpha() {
//            return alpha;
//        }
//
//        public void setAlpha(float alpha) {
//            if (alpha == this.alpha) {
//                return;
//            }
//            this.alpha = alpha;
//            repaint();
//        }
//
//        @Override
//        public void paint(Graphics g) {
//            Graphics2D g2d = (Graphics2D) g.create();
//            if (getAlpha() != 1) {
//                g2d.setComposite(AlphaComposite.SrcOver.derive(getAlpha()));
//            }
//            super.paint(g2d);
//            g2d.dispose();
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            Graphics2D g2d = (Graphics2D) g.create();
//            g2d.setColor(getBackground());
//            g2d.fillRect(0, 0, getWidth(), getHeight());
//            g2d.dispose();
//        }
//
//    }
//
//}
