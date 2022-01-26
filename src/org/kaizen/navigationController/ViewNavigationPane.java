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
 * The primary component used to present the actual components been managed
 * within the navigation stack
 */
public class ViewNavigationPane extends JPanel {

    protected enum TransitionType {
        PUSH, POP;
    }

    private FILOStack<ViewProxyPane> viewStack;

    public ViewNavigationPane() {
        setLayout(new BorderLayout());
        viewStack = new FILOStack<>();
    }

    protected NavigatableView navigatableView(ViewProxyPane view) {
        if (view != null && view.getView() instanceof NavigatableView) {
            return (NavigatableView) view.getView();
        }
        return null;
    }

    protected void willPresent(ViewProxyPane view) {
        NavigatableView delegate = navigatableView(view);
        if (delegate == null) {
            return;
        }
        delegate.willPresent();
    }

    protected void didPresent(ViewProxyPane view) {
        NavigatableView delegate = navigatableView(view);
        if (delegate == null) {
            return;
        }
        delegate.didPresent();
    }

    protected void willDismiss(ViewProxyPane view) {
        NavigatableView delegate = navigatableView(view);
        if (delegate == null) {
            return;
        }
        delegate.willDismiss();
    }

    protected void didDismiss(ViewProxyPane view) {
        NavigatableView delegate = navigatableView(view);
        if (delegate == null) {
            return;
        }
        delegate.didDismiss();
    }

    public void push(JComponent view) {
        System.out.println(">> Push (" + viewStack.size() + ")");
        ViewProxyPane currentView = viewStack.peekLast();
        willDismiss(currentView);

        ViewProxyPane nextView = new ViewProxyPane(view);

        viewStack.push(nextView);

        transitionBetween(currentView, nextView, TransitionType.PUSH);
    }

    public void pop() {
        System.out.println(">> Pop (" + viewStack.size() + ")");
        ViewProxyPane currentView = viewStack.pop();
        ViewProxyPane nextView = viewStack.peekLast();

        if (nextView != null) {
            transitionBetween(currentView, nextView, TransitionType.POP);
        } else {
            willDismiss(currentView);
            removeAll();
            didDismiss(currentView);
        }
    }

    public void popToRoot() {
        ViewProxyPane rootView = viewStack.peekFirst();
        ViewProxyPane currentView = viewStack.peekLast();
        // If first is null, last will be null
        if (rootView == null) {
            return;
        }

        System.out.println(">> popToRoot (" + viewStack.size() + ")");

        while (viewStack.peekLast() != rootView) {
            viewStack.pop();
        }

        transitionBetween(currentView, rootView, TransitionType.POP);
    }

    public void replaceWith(JComponent view) {
        ViewProxyPane last = viewStack.peekLast();
        push(view);
        if (last != null) {
            viewStack.remove(last);
        }
    }

    protected void transitionBetween(ViewProxyPane oldView, ViewProxyPane newView, TransitionType type) {
        willDismiss(oldView);
        willPresent(newView);

        switch (type) {
            case PUSH:
                transitionPushBetween(oldView, newView);
                break;
            case POP:
                transitionPopBetween(oldView, newView);
                break;
        }
    }

    protected void didTransitionBetween(ViewProxyPane oldView, ViewProxyPane newView, TransitionType type) {
        switch (type) {
            case PUSH:
                didTransitionBetweenPush(oldView, newView);
                break;
            case POP:
                didTransitionBetweenPop(oldView, newView);
                break;
        }
    }

    protected void didTransitionBetweenPush(ViewProxyPane oldView, ViewProxyPane newView) {
        didDismiss(oldView);
        didPresent(newView);
    }

    protected void didTransitionBetweenPop(ViewProxyPane oldView, ViewProxyPane newView) {
        didDismiss(oldView);
        didPresent(newView);
    }

    protected void animateTransition(TransitionType type, AnimtionTransitionProperties oldViewProperties, AnimtionTransitionProperties newViewProperties) {
        newViewProperties.getView().setAlpha(0);
        add(newViewProperties.getView());

        revalidate();

        Duration duration = Duration.ofMillis(300);
//            Duration duration = Duration.ofSeconds(5);

        DefaultAnimatableDuration animator = new DefaultAnimatableDuration(duration, Curves.SINE_IN_OUT.getCurve(), new AnimatableAdapter() {
            @Override
            public void animationCompleted(Animatable animator) {
                ViewProxyPane oldView = oldViewProperties.getView();
                ViewProxyPane newView = newViewProperties.getView();
                remove(oldView);
                // Reset the alpha values :P
                newView.setAlpha(1);
                oldView.setAlpha(1);

                didTransitionBetween(oldView, newView, type);

                revalidate();
                repaint();
            }

            @Override
            public void animationTimeChanged(AnimatableDuration animatable) {
                double progress = animatable.getProgress();

                ViewProxyPane newView = newViewProperties.getView();
                Range<Float> alphaInRange = newViewProperties.getAlphaRange();
                Range<Integer> moveInRange = newViewProperties.getMovementRange();

                newView.setLocation(moveInRange.valueAt(progress), 0);
                newView.setAlpha(alphaInRange.valueAt(progress));

                ViewProxyPane oldView = oldViewProperties.getView();
                Range<Float> alphaOutRange = oldViewProperties.getAlphaRange();
                Range<Integer> moveOutRange = oldViewProperties.getMovementRange();

                oldView.setLocation(moveOutRange.valueAt(progress), 0);
                oldView.setAlpha(alphaOutRange.valueAt(progress));
            }
        });
        animator.start();
    }

    protected void transitionPopBetween(ViewProxyPane oldView, ViewProxyPane newView) {
        int distant = (int) (getWidth() * 0.25);

        Range<Float> alphaInRange = new FloatRange(0f, 1f);
        Range<Integer> moveInRange = new IntRange(-distant, 0);

        Range<Float> alphaOutRange = new FloatRange(1f, 0f);
        Range<Integer> moveOutRange = new IntRange(0, distant);

        AnimtionTransitionProperties newAnimtionProperties = new AnimtionTransitionProperties(newView, alphaInRange, moveInRange);
        AnimtionTransitionProperties oldAnimtionProperties = new AnimtionTransitionProperties(oldView, alphaOutRange, moveOutRange);

        animateTransition(TransitionType.POP, oldAnimtionProperties, newAnimtionProperties);
    }

    protected void transitionPushBetween(ViewProxyPane oldView, ViewProxyPane newView) {
        if (oldView == null) {
            add(newView);
            didTransitionBetweenPush(oldView, newView);
            revalidate();
            repaint();
            return;
        }

        int distant = (int) (getWidth() * 0.25);

        Range<Float> alphaInRange = new FloatRange(0f, 1f);
        Range<Integer> moveInRange = new IntRange(distant, 0);

        Range<Float> alphaOutRange = new FloatRange(1f, 0f);
        Range<Integer> moveOutRange = new IntRange(0, -distant);

        AnimtionTransitionProperties newAnimtionProperties = new AnimtionTransitionProperties(newView, alphaInRange, moveInRange);
        AnimtionTransitionProperties oldAnimtionProperties = new AnimtionTransitionProperties(oldView, alphaOutRange, moveOutRange);

        animateTransition(TransitionType.PUSH, oldAnimtionProperties, newAnimtionProperties);
    }

    protected class AnimtionTransitionProperties {

        private ViewProxyPane view;
        private Range<Float> alphaRange;
        private Range<Integer> movementRange;

        public AnimtionTransitionProperties(ViewProxyPane view, Range<Float> alphaRange, Range<Integer> movementRange) {
            this.view = view;
            this.alphaRange = alphaRange;
            this.movementRange = movementRange;
        }

        public ViewProxyPane getView() {
            return view;
        }

        public Range<Float> getAlphaRange() {
            return alphaRange;
        }

        public Range<Integer> getMovementRange() {
            return movementRange;
        }
    }

    protected class ViewProxyPane extends JPanel {

        private float alpha = 1.0f;

        private JComponent view;

        public ViewProxyPane(JComponent view) {
            setOpaque(false);
            this.view = view;

            setLayout(new BorderLayout());
            add(view);
        }

        public JComponent getView() {
            return view;
        }

        public float getAlpha() {
            return alpha;
        }

        public void setAlpha(float alpha) {
            if (alpha == this.alpha) {
                return;
            }
            this.alpha = alpha;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            if (getAlpha() != 1) {
                g2d.setComposite(AlphaComposite.SrcOver.derive(getAlpha()));
            }
            super.paint(g2d);
            g2d.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }

    }

}
