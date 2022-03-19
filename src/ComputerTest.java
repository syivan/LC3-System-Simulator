/*
 * Unit tests for the Computer class.
 */

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Ivan Sy
 * @version 03/08/22
 */
class ComputerTest {

    // An instance of the Computer class to use in the tests.
    private Computer mComp;

    @BeforeEach
    void setUp() {
        mComp = new Computer();
    }

    /**
     * Test method for {@link Computer#executeBranch()}.
     */
    @Test
    void testExecuteBranch() {
        String program[] = {
                "0010000000000101",  // LD into R0 x39 which is ASCII 9
                "0000111000000011",  // BR jump regardless CHECK
                "1111000000100001",  // TRAP - vector x21 - OUT R0
                "0001000000111111",  // ADD - decrement R0 - the character
                "0000111111111001",  // BR - Loop back
                "1111000000100101",  // TRAP - vector x25 - HALT
                "0000000000111001",  // x39
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // PROGRAM COUNTER CHECK AFTER UNCONDITIONAL JUMP
        final char[] expectedPC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '1', '1', '0'};
        char[] resultPC = mComp.getPC().getBits();
        assertArrayEquals(expectedPC,resultPC);

        //CC CHECK == POSITIVE == 001
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '0', '1'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC,resultCC);
    }

    /**
     * Test method for {@link Computer#executeBranch()}.
     */
    @Test
    void testExecuteBranchPrint() {
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        String program[] = {
                "0010000000000110",  // LD into R0 x51 which is ASCII 3
                "0000010000000100",  // BR if zero skip down to code after the loop
                "1111000000100001",  // TRAP - vector x21 - OUT R0
                "0001000000111111",  // ADD - decrement R0 - the character
                "0001011011111111",  // ADD - decrement R3 - the counter
                "0000111111111011",  // BR - Loop back
                "1111000000100101",  // TRAP - vector x25 - HALT
                "0000000000110011",  // x51
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // PROGRAM COUNTER CHECK AFTER UNCONDITIONAL JUMP
        final char[] expectedPC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '1', '1', '1'};
        char[] resultPC = mComp.getPC().getBits();
        assertArrayEquals(expectedPC,resultPC);

        //PRINT CHECK TO SEE IF THE LOOP WORKS
        assertEquals("321", outputStreamCaptor.toString().trim());

        //CC CHECK == ZERO == 010
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '1', '0'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC,resultCC);
    }

    /**
     * Test method for {@link Computer#executeAdd()}.
     */
    @Test
    void testExecuteAddToR2() {
        String program[] = {
                "0001010001000010",		// R2 <- R1 + R2
                "1111000000100101"     // TRAP - vector x25 - HALT
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // CHECK R2 == 1 + 2 == 3
        assertEquals(3, mComp.getRegisters()[2].get2sCompValue());

        // CC CHECK == 001
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '0', '1'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }

    /**
     * Test method for {@link Computer#executeAdd()}.
     */
    @Test
    void testExecuteAddR2R3() {
        String program[] = {
                "0001100010000011",		// R4 <- R2 + R3
                "1111000000100101"     // TRAP - vector x25 - HALT
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // CHECK R4 == 2 + 5 == 5
        assertEquals(5, mComp.getRegisters()[4].get2sCompValue());

        // CC CHECK == 001
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '0', '1'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }

    /**
     * Test method for {@link Computer#executeAdd()}.
     */
    @Test
    void testExecuteAddEqualZero() {

        String program[] = {
                "0001001001111111",		// R1 <- R1 + -1
                "1111000000100101"     // TRAP - vector x25 - HALT
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // CHECK R2 == 1 + 2 == 3
        assertEquals(0, mComp.getRegisters()[1].get2sCompValue());

        // CC CHECK == 010
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '1', '0'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }

    /**
     * Test method for {@link Computer#executeAdd()}.
     */
    @Test
    void testExecuteAdd5() {

        String program[] = {
                "0001100100100101",		// R4 <- R4 + 5
                "1111000000100101"     // TRAP - vector x25 - HALT
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // CHECK R4 IS 9
        assertEquals(9, mComp.getRegisters()[4].get2sCompValue());

        // CC CHECK == 001
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '0', '1'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }

    /**
     * Test method for {@link Computer#executeAdd()}.
     */
    @Test
    void testExecuteAddNegative() {

        String program[] = {
                "0001000000111111",		// R0 <- R0 + -1
                "1111000000100101"     // TRAP - vector x25 - HALT
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // CHECK R2 == 0 + (-1) == -1
        assertEquals(-1, mComp.getRegisters()[0].get2sCompValue());

        // CC CHECK == 100
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '1', '0', '0'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }


    /**
     * Test method for {@link Computer#executeLoad()}.
     */
    @Test
    void testExecuteLoadZero() {
        String program[] = {
                "0010000000000010",  // LD into R0 x39 which is ASCII 9
                "0010001000001000",  // LD into R1 x-30
                "1111000000100101",  // TRAP - vector x25 - HALT
                "0000000000111001",  // x39
                "1111111111010000"	 //	x-30
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // R0 Check == 57 == x39
        assertEquals(57, mComp.getRegisters()[0].get2sCompValue());
        // R1 Check == 0 == x-30
        assertEquals(0, mComp.getRegisters()[1].get2sCompValue());

        // CC CHECK after loading x-30 into R1 = 010
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '1', '0'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }

    /**
     * Test method for {@link Computer#executeLoad()}.
     */
    @Test
    void testExecuteLoadPositive() {
        String program[] = {
                "0010000000000011",  // LD into R0 x36 which is ASCII 6
                "0010001000000001",  // LD into R1 x1B
                "1111000000100101",  // TRAP - vector x25 - HALT
                "0000000000011011",  // x1B
                "0000000000110110"	 //	x36
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // R0 CHECK == 54 == x36
        assertEquals(54, mComp.getRegisters()[0].get2sCompValue());
        // R1 CHECK == 27 == x1B
        assertEquals(27, mComp.getRegisters()[1].get2sCompValue());

        // CC CHECK after loading x1B into R1 = 001
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '0', '1'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }



    /**
     * Test method for {@link Computer#executeAnd()}.
     */
    @Test
    void testExecuteAndZero() {
        String program[] = {
                "0101011011100000",		// R3 <- R3 AND 0
                "1111000000100101"     // TRAP - vector x25 - HALT
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // R3 CHECK = 0
        assertEquals(0, mComp.getRegisters()[3].get2sCompValue());

        // CC CHECK = 010
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '1', '0'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }

    /**
     * Test method for {@link Computer#executeAnd()}.
     */
    @Test
    void testExecuteAndEqualRegister() {
        String program[] = {
                "0101100101000101",		// R4 <- R5 AND R5
                "1111000000100101"     // TRAP - vector x25 - HALT
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // R4 CHECK = 5
        assertEquals(5, mComp.getRegisters()[4].get2sCompValue());

        // CC CHECK = 001
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '0', '1'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }

    /**
     * Test method for {@link Computer#executeAnd()}.
     */
    @Test
    void testExecuteAndEqualImm5() {
        String program[] = {
                "0101101100100100",		// R5 <- R4 AND 4
                "1111000000100101"     // TRAP - vector x25 - HALT
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // R5 CHECK = 4
        assertEquals(4, mComp.getRegisters()[5].get2sCompValue());

        // CC CHECK = 001
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '0', '1'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }

    /**
     * Test method for {@link Computer#executeNot()}.
     */
    @Test
    void testExecuteNot5() {
        // NOTE: R5 contains #5 initially when the Computer is instantiated
        // So, iF we execute R4 <- NOT R5, then R4 should contain 1111 1111 1111 1010
        // AND CC should be 100
        String program[] = {
                "1001100101111111",    // R4 <- NOT R5
                "1111000000100101"     // TRAP - vector x25 - HALT
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // R4 CHECK == -6
        assertEquals(-6, mComp.getRegisters()[4].get2sCompValue());

        // R5 CHECK == 5
        final char[] expectedR5 = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '1', '0', '1'};
        char[] resultR5 = mComp.getRegisters()[5].getBits();
        assertArrayEquals(expectedR5, resultR5);

        // CC CHECK == 100
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '1', '0', '0'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }

    /**
     * Test method for {@link Computer#executeNot()}.
     */
    @Test
    void testExecuteNotR2() {
        String program[] = {
                "0001010001000010",		// R2 <- R1 + R2
                "1001001010111111",    // R1 <- NOT R2
                "1111000000100101"     // TRAP - vector x25 - HALT
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // R1 CHECK = -4
        assertEquals(-4, mComp.getRegisters()[1].get2sCompValue());

        // R2 CHECK = 3
        final char[] expectedR2 = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '1', '1'};
        char[] resultR2 = mComp.getRegisters()[2].getBits();
        assertArrayEquals(expectedR2, resultR2);

        // CC CHECK = 100
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '1', '0', '0'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }

    /**
     * Test method for {@link Computer#executeNot()}.
     */
    @Test
    void testExecuteNotNegative() {
        String program[] = {
                "0001001000111111",		// R1 <- R0 + (-1)
                "1001010001111111",    // R2 <- NOT R1
                "1111000000100101"     // TRAP - vector x25 - HALT
        };

        mComp.loadMachineCode(program);
        mComp.execute();

        // R1 CHECK = -1
        assertEquals(-1, mComp.getRegisters()[1].get2sCompValue());

        // R2 CHECK = 0
        assertEquals(0, mComp.getRegisters()[2].get2sCompValue());

        // CC CHECK = 010
        final char[] expectedCC = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '0', '0', '0', '0', '1', '0'};
        char[] resultCC = mComp.getCC().getBits();
        assertArrayEquals(expectedCC, resultCC);
    }

    /**
     * Test method for {@link Computer#executeTrap()}.
     */
    @Test
    void testExecuteTrap() {
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        String program[] = {
                "0010000000000010",  // LD into R0 x39 which is ASCII 9
                "1111000000100001",  // TRAP - vector x21 - OUT
                "1111000000100101",  // TRAP - vector x25 - HALT
                "0000000000111001",  // x39
        };

        mComp.loadMachineCode(program);
        mComp.execute();
        // System.out.println values are saved into outputStreamCaptor
        assertEquals("9", outputStreamCaptor.toString().trim());
    }

    /**
     * Test method for {@link Computer#executeNot()}.
     */
    @Test
    void testExecuteTrap2() {
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        String program[] = {
                "0010000000000011",  // LD into R0 x2D which is ASCII 45
                "0010001000000010",  // LD into R1 x39
                "1111000000100001",  // TRAP - vector x21 - OUT
                "1111000000100101",  // TRAP - vector x25 - HALT
                "0000000000101101",  // x2D
                "0000000000111001",  // x39
        };
        mComp.loadMachineCode(program);
        mComp.execute();
        assertEquals("-", outputStreamCaptor.toString().trim());
    }

    /**
     * Test method for {@link Computer#executeNot()}.
     */
    @Test
    void testExecuteTrapWithAdd() {
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        String program[] = {
                "0010000000000100",  // LD into R0 x2F which is ASCII 47
                "0010001000000100",  // LD into R1 x22
                "0001000000000001",	 // R0 <- R1 + R0
                "1111000000100001",  // TRAP - vector x21 - OUT
                "1111000000100101",  // TRAP - vector x25 - HALT
                "0000000000101111",  // x2F
                "0000000000100010",  // x22
        };

        mComp.loadMachineCode(program);
        mComp.execute();
        // R1 CHECK == x22 == 34
        assertEquals(34, mComp.getRegisters()[1].get2sCompValue());
        // R2 CHECK == x51 == 81
        assertEquals("Q", outputStreamCaptor.toString().trim());
    }

}

