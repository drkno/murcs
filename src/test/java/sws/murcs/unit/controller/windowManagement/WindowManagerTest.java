package sws.murcs.unit.controller.windowManagement;

import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.controller.windowManagement.WindowManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WindowManagerTest {
    @Mock
    public Stage stage;

    @Mock
    public Object controller;

    @Mock
    public Window parentWindow;

    @InjectMocks
    private Window mWindow1;
    @InjectMocks
    private Window mWindow2;
    @InjectMocks
    private Window mWindow3;
    @InjectMocks
    private Window mWindow4;
    @InjectMocks
    private Window mWindow5;
    private WindowManager windowManager;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        windowManager = new WindowManager();
        windowManager.addWindow(mWindow1);
        windowManager.addWindow(mWindow2);
        windowManager.addWindow(mWindow3);

        reset(stage);
        reset(controller);
        reset(parentWindow);

        // Check setup
        assertEquals(3, windowManager.getAllWindows().size());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testBringToTop1() throws Exception {
        assertEquals(2, windowManager.getWindowPosition(mWindow1));
        windowManager.bringToTop(mWindow1, false);
        assertEquals(0, windowManager.getWindowPosition(mWindow1));
        verify(stage, times(0)).toFront();
    }

    @Test
    public void testBringToTop2() throws Exception {
        assertEquals(2, windowManager.getWindowPosition(mWindow1));
        windowManager.bringToTop(mWindow1, true);
        assertEquals(0, windowManager.getWindowPosition(mWindow1));
        verify(stage, times(1)).toFront();
    }

    @Test
    public void testSendToBottom1() throws Exception {
        assertEquals(0, windowManager.getWindowPosition(mWindow3));
        windowManager.sendToBottom(mWindow3, false);
        assertEquals(2, windowManager.getWindowPosition(mWindow3));
        verify(stage, times(0)).toFront();
    }

    @Test
    public void testSendToBottom2() throws Exception {
        assertEquals(0, windowManager.getWindowPosition(mWindow3));
        windowManager.sendToBottom(mWindow3, true);
        assertEquals(2, windowManager.getWindowPosition(mWindow3));
        verify(stage, times(1)).toBack();
    }

    @Test
    public void testSendBackwards1() throws Exception {
        assertEquals(0, windowManager.getWindowPosition(mWindow3));
        windowManager.sendBackwards(mWindow3, false);
        assertEquals(1, windowManager.getWindowPosition(mWindow3));
        verify(stage, times(0)).toFront();
    }

    @Test
    public void testSendBackwards2() throws Exception {
        assertEquals(0, windowManager.getWindowPosition(mWindow3));
        windowManager.sendBackwards(mWindow3, true);
        assertEquals(1, windowManager.getWindowPosition(mWindow3));
        verify(stage, times(3)).toFront();
    }

    @Test
    public void testSendBackwards3() throws Exception {
        assertEquals(0, windowManager.getWindowPosition(mWindow3));
        windowManager.sendBackwards(mWindow3, 5, true);
        assertEquals(2, windowManager.getWindowPosition(mWindow3));
        verify(stage, times(3)).toFront();
    }

    @Test
    public void testSendBackwards4() throws Exception {
        assertEquals(0, windowManager.getWindowPosition(mWindow3));
        windowManager.sendBackwards(mWindow3, 2, true);
        assertEquals(2, windowManager.getWindowPosition(mWindow3));
        verify(stage, times(3)).toFront();
    }

    @Test
    public void testSendForwards1() throws Exception {
        assertEquals(2, windowManager.getWindowPosition(mWindow1));
        windowManager.sendForwards(mWindow1, false);
        assertEquals(1, windowManager.getWindowPosition(mWindow1));
        verify(stage, times(0)).toFront();
    }

    @Test
    public void testSendForwards2() throws Exception {
        assertEquals(2, windowManager.getWindowPosition(mWindow1));
        windowManager.sendForwards(mWindow1, true);
        assertEquals(1, windowManager.getWindowPosition(mWindow1));
        verify(stage, times(3)).toFront();
    }

    @Test
    public void testSendForwards3() throws Exception {
        assertEquals(2, windowManager.getWindowPosition(mWindow1));
        windowManager.sendForwards(mWindow1, 5, true);
        assertEquals(0, windowManager.getWindowPosition(mWindow1));
        verify(stage, times(3)).toFront();
    }

    @Test
    public void testSendForwards4() throws Exception {
        assertEquals(2, windowManager.getWindowPosition(mWindow1));
        windowManager.sendForwards(mWindow1, 2,true);
        assertEquals(0, windowManager.getWindowPosition(mWindow1));
        verify(stage, times(3)).toFront();
    }

    @Test
    public void testGetTop() throws Exception {
        Window window = windowManager.getTop();
        assertEquals(mWindow3, window);
    }

    @Test
    public void testRemoveWindow() throws Exception {
        assertTrue(windowManager.getAllWindows().contains(mWindow2));
        assertEquals(1, windowManager.getWindowPosition(mWindow2));
        windowManager.removeWindow(mWindow2);
        assertFalse(windowManager.getAllWindows().contains(mWindow2));
        assertEquals(-1, windowManager.getWindowPosition(mWindow2));
    }

    @Test
    public void testAddWindow() throws Exception {
        assertFalse(windowManager.getAllWindows().contains(mWindow4));
        windowManager.addWindow(mWindow4);
        assertTrue(windowManager.getAllWindows().contains(mWindow4));
        assertEquals(0, windowManager.getWindowPosition(mWindow4));
        assertEquals(1, windowManager.getWindowPosition(mWindow3));
        verify(stage, times(1)).toFront();
    }

    @Test
    public void testAddWindowToBack() throws Exception {
        assertFalse(windowManager.getAllWindows().contains(mWindow4));
        windowManager.addWindowToBack(mWindow4);
        assertTrue(windowManager.getAllWindows().contains(mWindow4));
        assertEquals(3, windowManager.getWindowPosition(mWindow4));
    }

    @Test
    public void testAddWindowWithPos() throws Exception {
        assertFalse(windowManager.getAllWindows().contains(mWindow4));
        windowManager.addWindowWithPos(mWindow4, 2);
        assertTrue(windowManager.getAllWindows().contains(mWindow4));
        assertEquals(2, windowManager.getWindowPosition(mWindow4));
        assertEquals(3, windowManager.getWindowPosition(mWindow1));
        verify(stage, times(4)).toFront();
    }

    @Test
    public void testGetWindowPosition() throws Exception {
        assertTrue(windowManager.getAllWindows().contains(mWindow2));
        assertEquals(1, windowManager.getWindowPosition(mWindow2));
    }

    @Test
    public void testSetWindowPosition1() throws Exception {
        assertEquals(2, windowManager.getWindowPosition(mWindow1));
        windowManager.setWindowPosition(mWindow1, -4,  false);
        assertEquals(0, windowManager.getWindowPosition(mWindow1));
        verify(stage, times(0)).toFront();
    }

    @Test
    public void testSetWindowPosition2() throws Exception {
        assertEquals(2, windowManager.getWindowPosition(mWindow1));
        windowManager.setWindowPosition(mWindow1, -4,  true);
        assertEquals(0, windowManager.getWindowPosition(mWindow1));
        verify(stage, times(3)).toFront();
    }

    @Test
    public void testSetWindowPosition3() throws Exception {
        assertEquals(0, windowManager.getWindowPosition(mWindow3));
        windowManager.setWindowPosition(mWindow3, 5,  false);
        assertEquals(2, windowManager.getWindowPosition(mWindow3));
        verify(stage, times(0)).toFront();
    }

    @Test
    public void testSetWindowPosition4() throws Exception {
        assertEquals(0, windowManager.getWindowPosition(mWindow3));
        windowManager.setWindowPosition(mWindow3, 5,  true);
        assertEquals(2, windowManager.getWindowPosition(mWindow3));
        verify(stage, times(3)).toFront();
    }

    @Test
    public void testSetWindowPosition5() throws Exception {
        assertEquals(0, windowManager.getWindowPosition(mWindow3));
        windowManager.setWindowPosition(mWindow3, 1,  false);
        assertEquals(1, windowManager.getWindowPosition(mWindow3));
        assertEquals(0, windowManager.getWindowPosition(mWindow2));
        assertEquals(2, windowManager.getWindowPosition(mWindow1));
        verify(stage, times(0)).toFront();
    }

    @Test
    public void testSetWindowPosition6() throws Exception {
        assertEquals(0, windowManager.getWindowPosition(mWindow3));
        windowManager.setWindowPosition(mWindow3, 1,  true);
        assertEquals(1, windowManager.getWindowPosition(mWindow3));
        assertEquals(0, windowManager.getWindowPosition(mWindow2));
        assertEquals(2, windowManager.getWindowPosition(mWindow1));
        verify(stage, times(3)).toFront();
    }

    @Test
    public void testGetAllWindows() throws Exception {
        List<Window> testList = new ArrayList<>();
        testList.add(mWindow3);
        testList.add(mWindow2);
        testList.add(mWindow1);
        assertTrue(windowManager.getAllWindows().containsAll(testList) &&
                testList.size() == windowManager.getAllWindows().size());
        assertTrue(testList.containsAll(windowManager.getAllWindows()) &&
                testList.size() == windowManager.getAllWindows().size());
        testList.add(mWindow4);
        assertFalse(windowManager.getAllWindows().containsAll(testList) &&
                testList.size() == windowManager.getAllWindows().size());
        assertFalse(testList.containsAll(windowManager.getAllWindows()) &&
                testList.size() == windowManager.getAllWindows().size());
    }
}
