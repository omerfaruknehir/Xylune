# Xylune 0.24.28

## Thinking control polish

- Completes Turkish localization for the new-conversation title and every thinking-level explanation shown by the composer.
- Removes the redundant instruction telling users how to release a slider.
- Makes supported thinking levels feel magnetic while dragging: nearby detents exert a smooth attraction instead of an instant snap, while release still settles cleanly onto the nearest supported level.
- Keeps continuous sliders continuous and limits magnetic behavior to the thinking-level control.
- Adds regression coverage for the magnetic attraction curve, the composer behavior, and English/Turkish resource parity.
