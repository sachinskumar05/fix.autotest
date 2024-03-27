Feature: Sample Order

  @Sample
  Scenario: Sample Order sent received
    Given ID001 Prepare FIX Messages using below data table
#   | SenderCompID    | MsgType | OrdQty  | OrdType | Price   | SecurityID  | SecurityIDSource  | Side | ExecInst |
      | H49           |H35      | 38      | 40      | 44      | 48          |  22               | 54   | 18
      | TEST_BANK_ONE |D        | 10000   | 1       | 100.99  | ABC_ISIN    |  4                | 1    | C
      | TEST_BANK_ONE |D        | 10000   | 1       | 100.99  | ABC_ISIN    |  4                | 2    | C
#    Then ID001 Send Messages