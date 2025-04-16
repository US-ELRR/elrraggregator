/** */
package com.deloitte.ellr.aggregator.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.deloitte.elrr.aggregator.dto.MessageVO;
import com.deloitte.elrr.aggregator.util.ValueObjectTestUtility;

class MessageVOTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @BeforeEach
  void setUp() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @AfterEach
  void tearDown() throws Exception {}

  @Test
  void test() {
    ValueObjectTestUtility.validateAccessors(MessageVO.class);
  }

  @Test
  void testToString() {
    assertNotNull(new MessageVO().toString());
  }
}
