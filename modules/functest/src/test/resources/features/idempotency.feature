Feature: Concurrency Request Test
	Testing if the request idempotency is satisfied


Scenario: Two users requesting the same reservation at the same time
	Given User creation user1
	And User creation user2
	When Both users try to request same reservation
	Then Only one gets it throught