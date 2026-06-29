# Code Reviews

Practice repository for code review exercises.

## Running tests

```sh
mvn test
```

## Current exercise

The open pull request adds a small Java hotel quote service. Review it as if it were a production change for a travel booking product: focus on correctness, edge cases, reliability, maintainability, API design, money/date handling, and whether the tests prove the behavior that matters.

## Business intent

The quote service calculates the estimated payable amount shown to a traveler before they commit to a booking. It is intended for shopping and checkout preview flows where the user needs a fast, explainable price breakdown before payment authorization.

This service does not own final payment capture, inventory reservation, or supplier reconciliation. The quote should be treated as a pricing preview that must be consistent with the rules available to this service at the time it is generated.

## Service expectations

- Return deterministic quotes for the same request and pricing rules.
- Keep the p95 calculation latency low enough for checkout usage; the target for this in-process version is under 50 ms excluding network or downstream calls.
- Prefer correctness over returning a misleading quote when required pricing inputs are missing or invalid.
- Expose enough quote breakdown fields for customer support and checkout UI to explain the final amount.
- Keep pricing rules auditable because incorrect quotes can directly affect customer trust and revenue.
