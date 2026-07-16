## Dependency Management

Life Reset OS currently uses manual dependency injection.

Architecture:

Screen
↓
ViewModel
↓
Repository
↓
DAO
↓
Room

Dependencies are provided through a simple AppContainer.

Reason:
Avoid unnecessary frameworks during MVP development while keeping the architecture scalable.